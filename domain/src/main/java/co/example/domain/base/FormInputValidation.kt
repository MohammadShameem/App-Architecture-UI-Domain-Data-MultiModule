package co.example.domain.base

import android.app.Application

import co.example.domain.usecase.remote.credential.LoginApiUseCase
import co.example.domain.R
import javax.inject.Inject

class FormInputValidation @Inject constructor(
    private val application: Application
){
    fun loginValidation(params: LoginApiUseCase.Params):ValidationResult{
        return if (params.mobile.length<10){
            ValidationResult.Failure(application.getString(R.string.message_invalid_phone))
        }else if (params.password.length<6){
            ValidationResult.Failure(application.getString(R.string.message_invalid_password))
        }else{
            ValidationResult.Success
        }
    }


}