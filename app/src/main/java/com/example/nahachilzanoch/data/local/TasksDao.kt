package com.example.nahachilzanoch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

    /**
     * Observes list of tasks.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Tasks")
    fun observeTasks(): Flow<List<Task>>

    /**
     * Observes a single task.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    fun observeTaskById(taskId: String): Flow<Task>

    /**
     * Select all tasks from the tasks table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM Tasks")
    suspend fun getTasks(): List<Task>

    /**
     * Select a task by id.
     *
     * @param taskId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Tasks WHERE entryid = :taskId")
    suspend fun getTaskById(taskId: String): Task?

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    suspend fun updateTask(task: Task): Int

    /**
     * Update the complete status of a task
     *
     * @param taskId id of the task
     * @param completed status to be updated
     */
    @Query("UPDATE tasks SET done = NOT done WHERE entryid = :taskId")
    suspend fun changeCompleted(taskId: String)

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Tasks WHERE entryid = :taskId")
    suspend fun deleteTaskById(taskId: String): Int
}
