package com.example.nahachilzanoch.ui.notifications

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import javax.inject.Inject


private val Context.mapDataStore: DataStore<Preferences> by preferencesDataStore(name = "taskId_toNotificationKey_map")
private val Context.rmapDataStore: DataStore<Preferences> by preferencesDataStore(name = "taskId_toNotificationKey_rmap")

// haha ya ebal)
class NotificationsManager @Inject constructor(
    private val context: Context,
    private val alarmScheduler: AlarmScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO) // TODO: eat that shit so nobody sees it
    private val taskIdToNotificationIdMap = mutableMapOf<String, Int>()
    private val notificationIdToTaskIdMap = mutableMapOf<Int, String>() // TODO: BiMap???
    private var safeNotificationId = 1
        get() = run {
            field++
            while (field in notificationIdToTaskIdMap) field++
            field
        }

    private val taskIdToNotificationIdMapDataStore = context.mapDataStore
    private val notificationIdToTaskIdMapDataStore = context.rmapDataStore


    init {
        scope.launch {
            taskIdToNotificationIdMapDataStore.data.last().asMap().forEach {
                taskIdToNotificationIdMap[it.key.name] = it.value as Int
            }
            notificationIdToTaskIdMapDataStore.data.last().asMap().forEach {
                notificationIdToTaskIdMap[it.key.name.toInt()] = it.value as String
            }
        }
    }

    private fun addToMap(taskId: String, notificationId: Int) {
        scope.launch {
            taskIdToNotificationIdMapDataStore.edit {
                it[intPreferencesKey(taskId)] = notificationId
            }
            notificationIdToTaskIdMapDataStore.edit {
                it[stringPreferencesKey(notificationId.toString())] = taskId
            }
            taskIdToNotificationIdMap[taskId] = notificationId
            notificationIdToTaskIdMap[notificationId] = taskId
        }
    }

    private fun clearMap() {
        scope.launch {
            taskIdToNotificationIdMapDataStore.edit { it.clear() }
            notificationIdToTaskIdMapDataStore.edit { it.clear() }
            notificationIdToTaskIdMap.clear()
            taskIdToNotificationIdMap.clear()
        }
    }

    private fun removeFromMap(taskId: String) {
        scope.launch {
            val notificationId = taskIdToNotificationIdMap[taskId]
            taskIdToNotificationIdMapDataStore.edit {
                it.remove( intPreferencesKey(taskId) )
            }
            notificationIdToTaskIdMapDataStore.edit {
                it.remove( stringPreferencesKey(notificationId.toString()) )
            }
            taskIdToNotificationIdMap.remove( taskId )
            notificationIdToTaskIdMap.remove( notificationId )
        }
    }

    fun refreshNotifications(list: List<Task>) {
        cancelAllNotifications()
        list.forEach { scheduleNotification(it) }
    }

    fun cancelNotification(task: Task) =
        cancelNotification( task.id )

    fun cancelNotification(id: String) {
        cancelNotification( taskIdToNotificationIdMap[id] ?: return )
    }

    private fun cancelNotification(id: Int) {
        alarmScheduler.cancelNotification(id)
        removeFromMap( notificationIdToTaskIdMap[id] ?: return )
    }

    fun cancelAllNotifications() =
        taskIdToNotificationIdMap.forEach { cancelNotification(it.key) }


    fun scheduleNotification(task: Task) {
        addToMap(task.id, safeNotificationId)
        alarmScheduler.scheduleNotification(
            task.deadlineDate ?: return,
            task.urgency.importance,
            task.text,
            taskIdToNotificationIdMap[task.id] ?: return
        )
    }

}

