package co.example.assets

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

open class CustomInputLayout : TextInputLayout{

    constructor(context: Context) : super(context){
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs){
        applyCustomFont(context)
        applyAttribute(attrs=attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr:Int) : super(context,attrs,defStyleAttr){
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        val customFont = FontsOverride.getTypeface(FontsOverride.regularFont, context)
        typeface = customFont
    }

    private fun applyAttribute(attrs: AttributeSet){
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomInputLayout,0,0)
        val validationType = typedArray.getString(R.styleable.CustomInputLayout_validation_type)
        val validationEnable = typedArray.getBoolean(R.styleable.CustomInputLayout_realtime_validation_enable,false)

        val emptyFieldMessage:String =  when(validationType){
            "1","0x1"->context.getString(R.string.give_right_phone_number)
            "2","0x2"->context.getString(R.string.give_right_phone_number)
            "3","0x3"->context.getString(R.string.give_right_password)
            else -> ""
        }

        val invalidInputMessage:String = when(validationType){
            "1","0x1"-> context.getString(R.string.give_ten_or_eleven_numbers)
            "2","0x2"->"Please provide valid email address"
            "3","0x3"-> context.getString(R.string.give_right_password)
            else->""
        }



        if (validationEnable){
            addOnEditTextAttachedListener {
                editText?.doAfterTextChanged {
                    it?.let {
                        if (it.isEmpty())  error = emptyFieldMessage
                        else{
                            when(validationType){
                                "1","0x1"->{
                                    error = if (it.length<10) invalidInputMessage
                                    else null
                                }
                                "2","0x2"->{}
                                "3","0x3"->{
                                    error = if (it.length<6) invalidInputMessage
                                    else null
                                }
                            }
                        }
                    }
                }
            }
        }

        typedArray.recycle()
    }
}