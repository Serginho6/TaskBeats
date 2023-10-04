package com.comunidadedevspace.taskbeats.presentation

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.local.Task

class TaskListAdapter(
    private val openTaskDetailView:(task: Task) -> Unit
) : ListAdapter<Task, TaskListViewHolder>(TaskListAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, openTaskDetailView)

        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            task.isSelected = isChecked
            holder.updateTaskAppearance(holder, isChecked)
        }
        holder.updateTaskAppearance(holder, task.isSelected)
    }

    companion object : DiffUtil.ItemCallback<Task>(){

        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title &&
                    oldItem.description == newItem.description
        }
    }
}

class TaskListViewHolder(private val view: View) : RecyclerView.ViewHolder(view){

    private val tvTitle: TextView = view.findViewById(R.id.tv_task_title)
    private val tvDesc: TextView = view.findViewById(R.id.tv_task_description)
    val checkTask: CheckBox = view.findViewById(R.id.checkbox_task)

    fun bind(
        task: Task,
        openTaskDetailView:(task: Task) -> Unit
    ) {
        tvTitle.text = task.title
        tvDesc.text = task.description
        checkTask.isChecked = task.isSelected

        view.setOnClickListener {
            openTaskDetailView.invoke(task)
        }
    }

    fun updateTaskAppearance(holder: TaskListViewHolder, isSelected: Boolean) {
        val context = holder.view.context  // Obtenha o contexto da View
        if (isSelected) {
            holder.tvTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvDesc.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.checkTask.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pink_500))
        } else {
            holder.tvTitle.paintFlags = holder.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvDesc.paintFlags = holder.tvDesc.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.checkTask.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brown_700))
        }
    }

}