package com.example.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ToDoListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.todo_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.taskList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TaskAdapter(taskList) { task -> deleteTask(task) }
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .add(R.id.container, AddTaskFragment())
                .addToBackStack("AddTaskFragment")
                .commit()
        }

        loadTasks()
    }

    private fun loadTasks() {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val database = FirebaseDatabase.getInstance(
            "https://to-do-list-example-8627b-default-rtdb.europe-west1.firebasedatabase.app/"
        )
        val target = database.reference
            .child(account?.id ?: "unknown_account").child("tasks")

        target.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                snapshot.children.forEach {
                    val taskName = it.getValue(String::class.java) ?: ""
                    val taskId = it.key ?: ""
                    taskList.add(Task(taskId, taskName))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun deleteTask(task: Task) {
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val database = FirebaseDatabase.getInstance(
            "https://to-do-list-example-8627b-default-rtdb.europe-west1.firebasedatabase.app/"
        )
        val target = database.reference
            .child(account?.id ?: "unknown_account")
            .child("tasks")
            .child(task.id)

        target.removeValue()
    }
}

data class Task(val id: String, val name: String)

class TaskAdapter(
    private val tasks: List<Task>,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.taskName)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.name
        holder.deleteButton.setOnClickListener { onDeleteClick(task) }
    }

    override fun getItemCount() = tasks.size
}