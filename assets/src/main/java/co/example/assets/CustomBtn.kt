package co.example.assets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomBtn : AppCompatButton {
    constructor(context: Context) : super(context){
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs){
        applyCustomFont(context)
        applyAttribute(attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr:Int) : super(context,attrs,defStyleAttr){
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        val customFont = FontsOverride.getTypeface(FontsOverride.mediumFont, context)
        typeface = customFont
    }

    private fun applyAttribute(attrs: AttributeSet){
        stateListAnimator = null
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.CustomBtn,0,0)
        val buttonBgColor = typedArray.getColor(R.styleable.CustomBtn_button_bg_color, Color.TRANSPARENT)
        val buttonShapeRadius = typedArray.getInt(R.styleable.CustomBtn_button_shape_radius,0)
        val buttonShapeType = typedArray.getString(R.styleable.CustomBtn_button_shape) //1(stroke) 2(rectangle)
        val buttonStrokeColor = typedArray.getColor(R.styleable.CustomBtn_button_stroke_color,Color.TRANSPARENT)
        val buttonStrokeWidth = typedArray.getInt(R.styleable.CustomBtn_button_stroke_width,0)
        val buttonRippleColor = typedArray.getColor(R.styleable.CustomBtn_button_ripple_color,Color.GREEN)
        val buttonDisableColor = typedArray.getColor(R.styleable.CustomBtn_button_disable_color,Color.GRAY)
        typedArray.recycle()

        val drawableBuilder = GradientDrawable()
        val contentColor = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(-android.R.attr.state_enabled)
            ),
            intArrayOf(
                buttonBgColor,
                buttonBgColor,
                buttonDisableColor
            )
        )
        drawableBuilder.color = contentColor

        when (buttonShapeType) {
            // stroke
            "1","0x1" -> {
                drawableBuilder.shape = GradientDrawable.RECTANGLE
                drawableBuilder.setStroke(buttonStrokeWidth*2,buttonStrokeColor)
                drawableBuilder.cornerRadius = (buttonShapeRadius * 4).toFloat()
            }

            // rectangle
           "2","0x2" ->{
               drawableBuilder.shape = GradientDrawable.RECTANGLE
               drawableBuilder.cornerRadius = (buttonShapeRadius * 4).toFloat()
           }
            // oval
            "3","0x3" -> drawableBuilder.shape = GradientDrawable.OVAL
        }



        val rippleDrawable = RippleDrawable(
            ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_activated)
                ),
                intArrayOf(
                    buttonRippleColor,
                    buttonRippleColor,
                    buttonRippleColor
                )
            ),
            drawableBuilder,
            null
        )
        drawableBuilder.invalidateSelf()
        rippleDrawable.invalidateSelf()
        background = rippleDrawable
    }
}
