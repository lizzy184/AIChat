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
class LogoutViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val userPreferences: UserPreferences
): ViewModel(){
    private val _logoutSuccess=MutableStateFlow(false)
    val logoutSuccess=_logoutSuccess.asStateFlow()
    private val _errorMessage= MutableStateFlow<String?>(null)
    val errorMessage=_errorMessage.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

fun logout(){
viewModelScope.launch {
    _errorMessage.value=null
    _logoutSuccess.value=false
when(val result=repository.logout()){
    is NetworkResult.Success->{
        userPreferences.clearTokens()
        _logoutSuccess.value=true

    }
    is NetworkResult.Error->{
        _errorMessage.value=result.message



    }

    is NetworkResult.Loading->{


    }













}






}








}











}