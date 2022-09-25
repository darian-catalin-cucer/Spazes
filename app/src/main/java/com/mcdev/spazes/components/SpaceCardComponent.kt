package com.mcdev.spazes.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.dolatkia.animatedThemeManager.AppTheme
import com.mcdev.spazes.R
import com.mcdev.spazes.SpazesThemeMode
import com.mcdev.spazes.changeLayersColor
import com.mcdev.spazes.databinding.SpaceItemV2Binding
import com.mcdev.spazes.theme.BaseTheme
import com.mcdev.spazes.theme.DarkTheme
import com.mcdev.spazes.theme.DefaultTheme
import com.mcdev.spazes.theme.LightTheme
import com.mcdev.tweeze.util.VerifiedBadge

class SpaceCardComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : CardView(context, attrs, defStyle) {

    private val binding: SpaceItemV2Binding = SpaceItemV2Binding.inflate(
        LayoutInflater.from(context),
        this, true
    )

    private var _displayName : String? = null
    private var _isVerified : Boolean = false
    private var _speakerImgUri : String? = null
    private var _speakerImgDrawable : Int? = null

    var theme : BaseTheme = DefaultTheme()
        set(value) {
            titleTextColor = value.textColor() // set the title color
            setDisplayNameColor(value.textColor()) // set display name text color
            binding.stateLiveView.changeLayersColor(value.lottieColor()) // changing lottie wave form color
            scheduledTextColor = value.textColor() // set scheduled date text color


            when (value.id()) {
                DefaultTheme().id() -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)
                    this.setDisplayName(_displayName!!, _isVerified, VerifiedBadge.WHITE)
                    if (_speakerImgUri != null && _speakerImgDrawable == null) {
                        this.setCardBgImageUri(_speakerImgUri)
                    } else if (_speakerImgDrawable != null && _speakerImgUri == null) {
                        this.setCardBgImageDrawable(R.drawable.spacessampleavi)
                    }
                    binding.participantCount.setTextColor(context.resources.getColor(R.color.gray_600, context.theme))
                }
                LightTheme().id() -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg)

                    this.setDisplayName(_displayName!!, _isVerified, VerifiedBadge.DEFAULT)
                    this.setCardBgColor(value.cardBg())
                    binding.participantCount.setTextColor(context.resources.getColor(R.color.gray_600, context.theme))
                }
                DarkTheme().id() -> {
                    binding.liveViews.background = ContextCompat.getDrawable(context, R.drawable.live_view_bg_dark)
                    binding.participantCount.setTextColor(context.resources.getColor(R.color.dark_mode_text_color, context.theme))
                    this.setDisplayName(_displayName!!, _isVerified, VerifiedBadge.WHITE)
                    this.setCardBgColor(value.cardBg())
                }

            }

            field = value
        }

    var title : String = "Anime & Chill #GoodVibes #AnimeSpaces"
        get() { // this will set the title in the text view even if no title is set
            binding.title.text = field
            return field
        }
        set(value) {
            binding.title.text = value
        }

    var titleTextColor : Int = (theme as BaseTheme).textColor()
        set(value) = binding.title.setTextColor(resources.getColor(value, context.theme))

    var scheduledTextColor : Int = (theme as BaseTheme).textColor()
        set(value) = binding.stateScheduledView.setTextColor(resources.getColor(value, context.theme))

    var isLive : Boolean = false
        set(value) {
            if (value) {
                binding.liveViews.visibility = View.VISIBLE
                binding.stateScheduledView.visibility = View.GONE
            } else {
                binding.liveViews.visibility = View.GONE
                binding.stateScheduledView.visibility = View.VISIBLE
            }
            field = value
        }

    var participantCount : CharSequence = ""
        set(value)  {
            binding.participantCount.text = value
            field = value
        }

    var scheduledDate : String? = null
        set(value) {
            binding.stateScheduledView.text = value
            field = value
        }

    init {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.NoSpaceComponent, defStyle, 0
        )

        title // this is necessary to set the default text view for the title in case it is omitted
        a.recycle()
    }


    fun setDisplayName(displayName: String?, verified: Boolean?, verifiedBadge: VerifiedBadge = VerifiedBadge.DEFAULT) {
        _displayName = displayName ?: " "
        _isVerified = verified ?: false
        binding.twitterDisplayNameView.setDisplayName(_displayName!!, _isVerified, verifiedBadge)
    }

    fun getDisplayName(): String {
        return _displayName!!
    }

    fun setDisplayNameColor(color: Int) {
        binding.twitterDisplayNameView.customizeDisplayName.setTextColor(resources.getColor(color, context.theme))
    }

    fun setSpeakerImageUri(uriString:  MutableList<String>) {
        binding.speakerAvi.setImageURI(uriString[0])
        when {
            uriString.size == 2 -> {
                binding.speakerAvi.setImageURI(uriString[1])
            }
            uriString.size >= 2 -> {
                    binding.speakerOneAvi.setImageURI(uriString[1])
                    binding.speakerTwoAvi.setImageURI(uriString[2])
            }
            else -> {
//                set these null values to prevent duplicates
                    binding.speakerOneAvi.setImageURI("null")
                    binding.speakerTwoAvi.setImageURI("null")
            }
        }
    }

    fun setSpeakerImageDrawable(resourceDrawable: Int) {
        _speakerImgDrawable = resourceDrawable
        binding.speakerAvi.setActualImageResource(resourceDrawable)
    }

    fun setCardBgImageUri(uriString: String?) {
        _speakerImgUri = uriString
        binding.bgSpeaker.setImageURI(uriString)
        binding.bgSpeaker.visibility = View.VISIBLE
    }

    fun setCardBgImageDrawable(resourceDrawable: Int) {
        binding.bgSpeaker.setActualImageResource(resourceDrawable)
        binding.bgSpeaker.visibility = View.VISIBLE
    }

    fun setCardBgColor(int: Int) {
        binding.bgSpeaker.visibility = View.GONE
        binding.root.setCardBackgroundColor(context.resources.getColor(int, context.theme))
    }
}