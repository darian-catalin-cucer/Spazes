package com.mcdev.spazes.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mcdev.spazes.databinding.BottomsheetLayoutBinding
import com.mcdev.spazes.databinding.FragmentLottieLoadingDialogBinding


class UserBottomSheetFragment: BottomSheetDialogFragment() {
    private lateinit var binding: BottomsheetLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetLayoutBinding.inflate(inflater, container, false)
        val root = binding.root


        return root
    }
}