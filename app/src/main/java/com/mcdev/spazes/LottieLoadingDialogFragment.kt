package com.mcdev.spazes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mcdev.spazes.databinding.FragmentLottieLoadingDialogBinding
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [LottieLoadingDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LottieLoadingDialogFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var messageParam: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLottieLoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            messageParam = it.getString("message")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLottieLoadingDialogBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.progressDialogMessage.text = messageParam

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LottieLoadingDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(message: String) =

            LottieLoadingDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(message, messageParam)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Objects.requireNonNull(Objects.requireNonNull(dialog?.window?.setBackgroundDrawable(ColorDrawable(
            Color.TRANSPARENT))))
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
    }
}