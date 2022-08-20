package co.example.assets

import android.content.Context
import android.graphics.Typeface
import java.util.HashMap

object FontsOverride {
    const val regularFont = "fonts/Inter-Regular.ttf"
    const val boldFont = "fonts/Inter-Bold.ttf"
    const val semiBoldFont = "fonts/Inter-SemiBold.ttf"
    const val mediumFont = "fonts/Inter-Medium.ttf"
    const val italicFont = "fonts/Inter-Italic.ttf"

    fun setDefaultFont(context: Context, staticTypefaceFieldName: String, fontAssetName: String) {
        val regular = Typeface.createFromAsset(context.assets, fontAssetName)
        replaceFont(staticTypefaceFieldName, regular)
    }


    private fun replaceFont(staticTypefaceFieldName: String, newTypeface: Typeface) {
        try {
            val staticField = Typeface::class.java.getDeclaredField(staticTypefaceFieldName)
            staticField.isAccessible = true
            staticField.set(null, newTypeface)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private val fontCache = HashMap<String, Typeface>()
    fun getTypeface(fontname: String, context: Context): Typeface? {
        var typeface: Typeface? = fontCache[fontname]

        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.assets, fontname)
            } catch (e: Exception) {
                return null
            }

            fontCache[fontname] = typeface
        }

        return typeface
    }


}