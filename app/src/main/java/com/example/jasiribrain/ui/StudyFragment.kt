package com.example.jasiribrain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.databinding.FragmentStudyBinding

class StudyFragment: Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    private val studyMethodFrag = StudyMethodFrag()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studyMethodCheck()
    }

    private fun studyMethodCheck() {
        binding.run {
            pomodoroBtn.setOnClickListener {
                JasiriDataHolder.setStudyMethodSelect(Constants.POMODORO_SEL)
                studyFragOpener(studyMethodFrag)
            }
            gtdBtn.setOnClickListener {
                JasiriDataHolder.setStudyMethodSelect(Constants.GTD_SEL)
                studyFragOpener(studyMethodFrag)
            }
            forceStartBtn.setOnClickListener {
                JasiriDataHolder.setStudyMethodSelect(Constants.FORCE_START_SEL)
                studyFragOpener(studyMethodFrag)
            }
        }
    }

    private fun studyFragOpener(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.methodFragment, fragment)
            .commitNow()
    }

}