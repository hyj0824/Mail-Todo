package com.mailtodo.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mailtodo.app.databinding.ActivityMainBinding
import com.mailtodo.app.ui.auth.EmailAccountSetupActivity
import com.mailtodo.app.ui.auth.MicrosoftAuthActivity
import com.mailtodo.app.ui.adapter.EmailAdapter
import com.mailtodo.app.ui.adapter.TodoAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var emailAdapter: EmailAdapter
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()

        // Start background sync
        viewModel.startBackgroundSync()
    }

    private fun setupUI() {
        // Setup email RecyclerView
        emailAdapter = EmailAdapter { email ->
            viewModel.createTodoFromEmail(email)
        }
        binding.recyclerViewEmails.apply {
            adapter = emailAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Setup todo RecyclerView
        todoAdapter = TodoAdapter(
            onTodoClick = { todo ->
                viewModel.toggleTodoCompletion(todo)
            },
            onTodoDelete = { todo ->
                viewModel.deleteTodo(todo)
            }
        )
        binding.recyclerViewTodos.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Setup buttons
        binding.buttonAddAccount.setOnClickListener {
            startActivity(Intent(this, EmailAccountSetupActivity::class.java))
        }

        binding.buttonConnectMicrosoft.setOnClickListener {
            startActivity(Intent(this, MicrosoftAuthActivity::class.java))
        }

        binding.buttonRefresh.setOnClickListener {
            viewModel.refreshEmails()
        }

        binding.buttonSyncTodos.setOnClickListener {
            viewModel.syncTodosWithMicrosoft()
        }

        // Setup tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("邮件"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("待办事项"))

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.recyclerViewEmails.visibility = android.view.View.VISIBLE
                        binding.recyclerViewTodos.visibility = android.view.View.GONE
                    }
                    1 -> {
                        binding.recyclerViewEmails.visibility = android.view.View.GONE
                        binding.recyclerViewTodos.visibility = android.view.View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupObservers() {
        // Observe emails
        viewModel.emails.observe(this) { emails ->
            emailAdapter.submitList(emails)
        }

        // Observe todos
        viewModel.todos.observe(this) { todos ->
            todoAdapter.submitList(todos)
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Observe sync status
        viewModel.syncStatus.observe(this) { status ->
            binding.textSyncStatus.text = status
        }
    }
}
