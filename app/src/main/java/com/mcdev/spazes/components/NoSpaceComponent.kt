package com.mcdev.spazes.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mcdev.spazes.R
import com.mcdev.spazes.databinding.NoSpaceComponentBinding

/**
 * TODO: document your custom view class.
 */
class NoSpaceComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val binding: NoSpaceComponentBinding = NoSpaceComponentBinding.inflate(
        LayoutInflater.from(
            context
        ), this, true
    )

    var lottieRawResource = R.raw.astronaut
        set(value)  {
            binding.lottieAnimView.setAnimation(value)
        }

    var lottieMessage = resources.getString(R.string.no_spaces_found)
        set(value) {
            binding.lottieMessageTextView.text = value
            field = value
        }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NoSpaceComponent, defStyle, 0
        )

        a.recycle()
    }
}
