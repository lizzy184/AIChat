package com.example.aichatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichatapp.data.UserPreferences
import com.example.aichatapp.network.NetworkResult
import com.example.aichatapp.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel@Inject constructor(
private val repository: ChatRepository,
    private val userPreferences: UserPreferences



): ViewModel() {

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess=_loginSuccess.asStateFlow()
    fun login(username: String,password: String){




        viewModelScope.launch {
            when(val result=repository.login(username,password)){
                is NetworkResult.Success->{
                    userPreferences.saveUserId(result.data.user_id)
_loginSuccess.value=true
                }
                is NetworkResult.Error->{}

                is NetworkResult.Loading->{}



            }








        }




    }








}