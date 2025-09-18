package com.mailtodo.app.ui.auth

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mailtodo.app.databinding.ActivityMicrosoftAuthBinding
import com.mailtodo.app.service.MicrosoftTodoService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MicrosoftAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMicrosoftAuthBinding

    @Inject
    lateinit var microsoftTodoService: MicrosoftTodoService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMicrosoftAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
    }

    private fun setupWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Check if authentication is successful
                if (url?.contains("access_token") == true || url?.contains("code=") == true) {
                    // Handle successful authentication
                    handleAuthSuccess()
                }
            }
        }

        // Load Microsoft authentication URL
        val authUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?" +
                "client_id=YOUR_CLIENT_ID&" +
                "response_type=code&" +
                "redirect_uri=YOUR_REDIRECT_URI&" +
                "scope=https://graph.microsoft.com/Tasks.ReadWrite"

        binding.webView.loadUrl(authUrl)
    }

    private fun handleAuthSuccess() {
        Toast.makeText(this, "Microsoft认证成功", Toast.LENGTH_SHORT).show()
        finish()
    }
}
