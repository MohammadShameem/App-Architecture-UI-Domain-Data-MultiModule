package co.example.common.extfun

import co.example.common.BuildConfig
import co.example.common.constant.AppConstant

/**
 * @return A String that shows if the app is in Dev, Staging, Or Live Mode
 * */
fun getBuildType(): String{
    return when (BuildConfig.BUILD_TYPE) {
        AppConstant.appTypeDebug -> return "Dev"
        AppConstant.appTypeStaging -> return "Staging"
        AppConstant.appTypeRelease -> return "Live"
        else -> "Unknown Build"
    }
}