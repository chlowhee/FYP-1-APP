package com.example.jasiribrain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.databinding.FragmentStudyBinding

class StudyFragment: Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    private lateinit var pomodoroStudyFrag: PomodoroStudyFrag
    private val forceStartStudyFrag = ForceStartStudyFrag()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pomodoroCheck()
    }

    private fun pomodoroCheck() {
        binding.pomodoroBtn.setOnClickListener {
            //test
            childFragmentManager.beginTransaction().replace(R.id.methodFragment, forceStartStudyFrag).setReorderingAllowed(true).commitNow()
        }
    }

}