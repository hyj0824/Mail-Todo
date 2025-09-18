package com.mailtodo.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mailtodo.app.data.model.EmailAccount
import com.mailtodo.app.repository.MailTodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailSetupViewModel @Inject constructor(
    private val repository: MailTodoRepository
) : ViewModel() {

    suspend fun saveAccount(account: EmailAccount) {
        repository.addEmailAccount(account)
    }
}
