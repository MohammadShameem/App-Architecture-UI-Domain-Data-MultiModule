package co.example.common.extfun

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import co.example.common.constant.Intentkey

fun Activity.navigateToDashboardActivity(){
    startActivity(Intent()
        .setClassName(this,"co.jatri.dashboard.DashboardActivity"))
}

fun Activity.navigateToSettingsActivity(){
    startActivity(Intent().setClassName(this,"co.jatri.settings.SettingsActivity"))
}

fun Activity.navigateProfileActivity(resultLauncher: ActivityResultLauncher<Intent>,appName:String,version:String, type:String) =
    resultLauncher.launch(Intent().
    setClassName(this,"co.jatri.profile.ProfileActivity")
        .putExtra(Intentkey.APP_NAME,appName)
        .putExtra(Intentkey.APP_VERSION,version)
        .putExtra(Intentkey.APP_TYPE,type)
    )

fun Activity.navigateEntryActivity(){
    startActivity(Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        .setClassName(this,"co.jatri.intracitycounterautosync.ui.entry.EntryActivity"))
    finish()
}

