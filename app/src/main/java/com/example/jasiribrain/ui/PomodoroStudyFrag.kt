package com.example.jasiribrain.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiribrain.databinding.FragStudyPomodoroBinding

class PomodoroStudyFrag: Fragment() {

    private var _binding: FragStudyPomodoroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragStudyPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        initPomodoroExitBtn()
    }

    private fun initPomodoroExitBtn() {
        binding.pomodoroExitBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commitNow()
        }
    }

    //TODO: custom countdown timer but default 25 mins

}