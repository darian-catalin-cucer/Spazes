package com.mcdev.spazes.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getIntOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.TritoneComponentBinding

class TritoneComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {
    private val binding: TritoneComponentBinding = TritoneComponentBinding.inflate(
        LayoutInflater.from(context),
        this, true
    )


    var icon : Int = 0
        set(value) {
            binding.tritoneIv.setImageDrawable(ResourcesCompat.getDrawable(resources, value, context.theme))
        }

    var bgColor: Int = 0
        set(value) {
            binding.root.setCardBackgroundColor(context.resources.getColor(value, context.theme))
            field = value
        }

    init {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.TritoneComponent, defStyle, 0
        )

        val getIcon = a.getResourceId(R.styleable.TritoneComponent_icon, R.drawable.settings)
        val getBgColor = a.getResourceId(R.styleable.TritoneComponent_bgColor, R.color.tritone_purple_bg)

        this.icon = getIcon
        this.bgColor = getBgColor

        a.recycle()
    }
}