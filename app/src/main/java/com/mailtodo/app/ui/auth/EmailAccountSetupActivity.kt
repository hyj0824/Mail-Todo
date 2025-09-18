package com.mailtodo.app.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mailtodo.app.data.model.AccountType
import com.mailtodo.app.data.model.EmailAccount
import com.mailtodo.app.databinding.ActivityEmailAccountSetupBinding
import com.mailtodo.app.service.EmailService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class EmailAccountSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailAccountSetupBinding
    private val viewModel: EmailSetupViewModel by viewModels()

    @Inject
    lateinit var emailService: EmailService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailAccountSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.spinnerAccountType.setSelection(0) // Default to Outlook

        binding.buttonSave.setOnClickListener {
            saveAccount()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.spinnerAccountType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateServerSettings(position)
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun updateServerSettings(position: Int) {
        val accountType = when (position) {
            0 -> AccountType.OUTLOOK
            1 -> AccountType.QQ
            else -> AccountType.GENERIC
        }

        val (server, port) = emailService.getImapConfig(accountType)
        binding.editTextImapServer.setText(server)
        binding.editTextImapPort.setText(port.toString())
    }

    private fun saveAccount() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()
        val imapServer = binding.editTextImapServer.text.toString().trim()
        val imapPort = binding.editTextImapPort.text.toString().toIntOrNull() ?: 993

        if (email.isEmpty() || password.isEmpty() || imapServer.isEmpty()) {
            Toast.makeText(this, "请填写所有必需字段", Toast.LENGTH_SHORT).show()
            return
        }

        val accountType = when (binding.spinnerAccountType.selectedItemPosition) {
            0 -> AccountType.OUTLOOK
            1 -> AccountType.QQ
            else -> AccountType.GENERIC
        }

        val account = EmailAccount(
            id = UUID.randomUUID().toString(),
            emailAddress = email,
            accountType = accountType,
            imapServer = imapServer,
            imapPort = imapPort,
            username = email,
            password = password // 在生产环境中应该加密存储
        )

        lifecycleScope.launch {
            try {
                binding.buttonSave.isEnabled = false
                binding.progressBar.visibility = android.view.View.VISIBLE

                // Test connection first
                val testEmails = emailService.fetchEmails(account, 1)

                // If successful, save account
                viewModel.saveAccount(account)

                Toast.makeText(this@EmailAccountSetupActivity, "账户添加成功", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@EmailAccountSetupActivity, "连接失败: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.buttonSave.isEnabled = true
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun setupObservers() {
        // Observe any state changes if needed
    }
}
