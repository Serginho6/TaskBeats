package com.comunidadedevspace.taskbeats.presentation

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Paint
import android.preference.PreferenceManager
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
    private val context: Context,
    private val openTaskDetailView:(task: Task) -> Unit
) : ListAdapter<Task, TaskListViewHolder>(TaskListAdapter) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val checkedExercisesSet: MutableSet<String> = mutableSetOf()

    init {
        val checkedExercises = sharedPreferences.getStringSet("checkedExercises", mutableSetOf())
        if (checkedExercises != null) {
            checkedExercisesSet.addAll(checkedExercises)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_task, parent, false)

        return TaskListViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, openTaskDetailView)

        holder.checkTask.setOnCheckedChangeListener(null)

        holder.checkTask.isChecked = checkedExercisesSet.contains(task.title)

        holder.checkTask.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedExercisesSet.add(task.title)
            } else {
                checkedExercisesSet.remove(task.title)
            }

            sharedPreferences.edit().putStringSet("checkedExercises", checkedExercisesSet).apply()

            holder.updateTaskAppearance(holder, isChecked)
        }

        holder.updateTaskAppearance(holder, holder.checkTask.isChecked)
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