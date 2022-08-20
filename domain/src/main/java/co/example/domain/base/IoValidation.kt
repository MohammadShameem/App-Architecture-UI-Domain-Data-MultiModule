package co.example.domain.base

import android.text.TextUtils
import android.util.Patterns
import co.example.domain.usecase.remote.credential.LoginApiUseCase
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class IoValidation @Inject constructor(){
    fun loginDataValidation(params : LoginApiUseCase.Params) : ValidationResultV2 {
        return if (params.mobile.isEmpty()) ValidationResultV2.Failure(LoginIoResult.EmptyPhoneNumber)
        else if (!isPhoneNumberValid(params.mobile)) ValidationResultV2.Failure(LoginIoResult.InvalidPhoneNumber)
        else if (params.password.isEmpty()) ValidationResultV2.Failure(LoginIoResult.EmptyPassword)
        else if (params.password.length < 6) ValidationResultV2.Failure(LoginIoResult.InvalidPassword)
        else ValidationResultV2.Success
    }


    private fun isPhoneNumberValid(number: String): Boolean {
        val pattern: Pattern = Pattern.compile( "((0|01|\\+88|\\+88\\s*\\(0\\)|\\+88\\s*0)\\s*)?1(\\s*[0-9]){9}")
        val matcher: Matcher = pattern.matcher(number)
        return matcher.matches()
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}