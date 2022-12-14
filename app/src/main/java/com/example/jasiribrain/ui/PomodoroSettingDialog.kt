package com.example.jasiribrain.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.databinding.DialogPomodoroSettingBinding

class PomodoroSettingDialog: Fragment() {

    private var _binding: DialogPomodoroSettingBinding? = null
    private val binding get() = _binding!!
    private val TAG = "PomoSettingDialog"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogPomodoroSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        okButtonInit()
        initAllPicker()
    }

    private fun okButtonInit() {
        binding.okBtnPomoSettings.setOnClickListener {
            val pomoDuration = binding.pomodoroPickerMin.value
            val breakDuration = binding.breakPickerMin.value
            val numCycles = binding.numCyclesSpinner.selectedItem.toString()
            Log.d(TAG, "Pomodoro, Break, Cycles: $pomoDuration, $breakDuration, $numCycles")
            JasiriDataHolder.setPomodoroDuration(pomoDuration)
            JasiriDataHolder.setBreakDuration(breakDuration)
            JasiriDataHolder.setNumCycles(numCycles.toInt())
            parentFragmentManager.beginTransaction().remove(this).commitNow()
        }
    }

    private fun initPicker(min: Int, max: Int, p: NumberPicker) {
        p.minValue = min
        p.maxValue = max
        p.setFormatter { i -> String.format("%02d", i) }
    }

    private fun initAllPicker() {
        initPicker(25, 60, binding.pomodoroPickerMin)
        initPicker(5, 10, binding.breakPickerMin)
    }

}