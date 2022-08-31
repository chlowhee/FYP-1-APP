package com.example.jasiribrain.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiribrain.databinding.FragStudyForceStartBinding

class ForceStartStudyFrag: Fragment() {

    private var _binding: FragStudyForceStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragStudyForceStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        initForceStartExitBtn()
    }

    private fun initForceStartExitBtn() {
        binding.forceStartExitBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commitNow()
        }
    }

    //TODO: 2 MIN countdown timer

}