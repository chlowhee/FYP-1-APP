package com.example.jasiribrain.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.databinding.DialogPomodoroSettingBinding

class PomodoroSettingDialog: DialogFragment() {

    private var _binding: DialogPomodoroSettingBinding? = null
    private val binding get() = _binding!!
    val TAG = "PomoSettingDialog"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setCanceledOnTouchOutside(true)
        _binding = DialogPomodoroSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "JasiriDataHolder pomo: ${JasiriDataHolder.pomodoroDuration.value}")

        okButtonInit()
        initAllPicker()
    }

    private fun okButtonInit() {
        binding.okBtnPomoSettings.setOnClickListener {
            val pomoDuration = binding.pomodoroPicker.value
            val breakDuration = binding.breakPicker.value
            val numCycles = binding.numCyclesSpinner.selectedItem.toString()
            Log.d(TAG, "Pomodoro, Break, Cycles: $pomoDuration, $breakDuration, $numCycles")
            if (binding.eyeDetectionSwitch.isChecked) {
                JasiriDataHolder.setEyeDetectionToggle(true)
            } else {
                JasiriDataHolder.setEyeDetectionToggle(false)
            }
            JasiriDataHolder.setIsPomodoroBreak(false)
            JasiriDataHolder.setBreakDuration(breakDuration)
            JasiriDataHolder.setNumCycles(numCycles.toInt())
            JasiriDataHolder.setNumCyclesCounter(numCycles.toInt())
            JasiriDataHolder.setPomodoroDuration(pomoDuration)
            parentFragmentManager.beginTransaction().remove(this).commitNow()
        }
    }

    private fun initPicker(min: Int, max: Int, p: NumberPicker) {
        p.minValue = min
        p.maxValue = max
        p.setFormatter { i -> String.format("%02d", i) }
    }

    private fun initAllPicker() {
        initPicker(1, 60, binding.pomodoroPicker)    //25-60    PUT TILL 1 FOR DEMONSTRATING PURPOSES
        initPicker(1, 10, binding.breakPicker)       //5-10
        binding.pomodoroPicker.value = JasiriDataHolder.pomodoroDuration.value
        binding.breakPicker.value = JasiriDataHolder.breakDuration.value
    }

}