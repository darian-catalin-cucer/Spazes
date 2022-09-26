package com.mcdev.spazes.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
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

    }
}