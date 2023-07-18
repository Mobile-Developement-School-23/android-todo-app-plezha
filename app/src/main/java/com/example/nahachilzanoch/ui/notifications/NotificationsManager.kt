package com.example.nahachilzanoch.ui.notifications

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nahachilzanoch.data.local.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


private val Context.mapDataStore: DataStore<Preferences> by preferencesDataStore(name = "taskId_toNotificationKey_map")

// haha ya ebal)
class NotificationsManager @Inject constructor(
    private val context: Context,
    private val alarmScheduler: AlarmScheduler
) {
    private val scope = CoroutineScope(Dispatchers.IO) // TODO: is it ok?
    private val taskIdToNotificationIdMap = mutableMapOf<String, Int>()
    private val notificationIdToTaskIdMap = mutableMapOf<Int, String>() // TODO: BiMap???
    private var safeNotificationId = 1
        get() = run {
            field++
            while (field in notificationIdToTaskIdMap) field++
            field
        }

    private val taskIdToNotificationIdMapDataStore = context.mapDataStore


    init {
        scope.launch() {
            taskIdToNotificationIdMapDataStore.data.last().asMap().forEach {
                taskIdToNotificationIdMap[it.key.name] = it.value as Int
                notificationIdToTaskIdMap[it.value as Int] = it.key.name
            }
        }
        Thread.sleep(100)
    }

    private fun addToMap(taskId: String, notificationId: Int) {
        taskIdToNotificationIdMap[taskId] = notificationId
        notificationIdToTaskIdMap[notificationId] = taskId
        scope.launch {
            taskIdToNotificationIdMapDataStore.edit {
                it[intPreferencesKey(taskId)] = notificationId
            }
        }
    }

    private fun clearMap() {
        scope.launch {
            taskIdToNotificationIdMapDataStore.edit { it.clear() }
        }
        notificationIdToTaskIdMap.clear()
        taskIdToNotificationIdMap.clear()
    }

    private fun removeFromMap(taskId: String) {
        val notificationId = taskIdToNotificationIdMap[taskId]
        scope.launch {
            taskIdToNotificationIdMapDataStore.edit {
                it.remove( intPreferencesKey(taskId) )
            }
        }
        taskIdToNotificationIdMap.remove( taskId )
        notificationIdToTaskIdMap.remove( notificationId )
    }

    fun refreshNotifications(list: List<Task>) {
        Log.d("", "refreshed notifs from $list")
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
        Log.d("", "scheduling $task from NotificationsManager")
        addToMap(task.id, safeNotificationId)
        if (!task.isDone && task.deadlineDate != null && task.deadlineDate > Calendar.getInstance().time.time) {
            alarmScheduler.scheduleNotification(
                task.deadlineDate,
                task.urgency.importance,
                task.text,
                taskIdToNotificationIdMap[task.id] ?: return
            )
        }
    }

}

