package co.example.domain.base


sealed class ValidationResultV2{
    object Success : ValidationResultV2()
    data class Failure<T>(val ioErrorResult: T) : ValidationResultV2()
}

sealed class LoginIoResult{
    object EmptyPhoneNumber:LoginIoResult()
    object InvalidPhoneNumber:LoginIoResult()
    object EmptyPassword:LoginIoResult()
    object InvalidPassword:LoginIoResult()
}