package com.example.nahachilzanoch.ui.list.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nahachilzanoch.R
import com.example.nahachilzanoch.data.local.Task
import com.example.nahachilzanoch.data.local.Urgency
import com.example.nahachilzanoch.util.getDateAndTimeString

class TasksAdapter(
    private val onCheckBoxClicked: (Task) -> Unit,
    private val onItemClicked: (Task, View) -> Unit
) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(
        itemView: View,
        private val onCheckBoxClicked: (Task) -> Unit,
        private val onItemClicked: (Task, View) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
        private val dateTextView = itemView.findViewById<TextView>(R.id.dateText)
        private val taskTextView = itemView.findViewById<TextView>(R.id.taskText)
        private val dateLine = itemView.findViewById<LinearLayout>(R.id.dateLine)
        private val urgencyImage = itemView.findViewById<ImageView>(R.id.urgencyImage)
        private val urgencyTextView = itemView.findViewById<TextView>(R.id.urgencyTextView)

        fun bind(task: Task) {
            checkbox.isChecked = task.isDone
            if (task.deadlineDate == null)
                dateLine.isVisible = false
            else {
                dateLine.isVisible = true
                dateTextView.text = task.deadlineDate.getDateAndTimeString()
            }

            urgencyImage.isVisible = task.urgency == Urgency.LOW

            urgencyTextView.isVisible = task.urgency == Urgency.URGENT

            taskTextView.text = task.text

            itemView.setOnClickListener {
                onItemClicked(task, itemView)
            }

            checkbox.setOnClickListener {
                onCheckBoxClicked(task)
            }
        }
    }

    var tasks = listOf<Task>()
        set(value) {
            DiffUtil.calculateDiff(
                TasksDiffCallback(
                    field,
                    value,
                )
            ).dispatchUpdatesTo(this)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.task_layout,
                    parent,
                    false
                ),
            onCheckBoxClicked,
            onItemClicked
        )

    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

}