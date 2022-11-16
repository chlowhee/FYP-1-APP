package com.example.jasiribrain.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.common.BluetoothStatusListener
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.databinding.FragStudyMethodsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StudyMethodFrag: Fragment() {

    private var _binding: FragStudyMethodsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "studyMethodsFrag"

    @Inject lateinit var controller: BluetoothController

    private lateinit var mCountDownTimer: CountDownTimer
    private var mTimeLeftMillis = Constants.FORCE_START_TIME_MS
    var builder: AlertDialog.Builder? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragStudyMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { _, _ -> true }

        studyMethodUiInit()
        initStudyMethodExitBtn()
        timeStartInit()
        testbtnInit()
    }

    private fun studyMethodUiInit() {
        val sel = JasiriDataHolder.studyMethodSelect.value
        binding.run {
            when (sel) {
                Constants.POMODORO_SEL -> {
                    studyMethodTitle.text = getString(R.string.pomodoro_title)
                    descriptorinator.text = getString(R.string.time_to_focus)
                    cyclesLeftDescript.visibility = View.VISIBLE
                    timerSettings.visibility = View.VISIBLE
                }
                Constants.GTD_SEL -> {
                    studyMethodTitle.text = getString(R.string.gtd_title)
                    descriptorinator.text = getString(R.string.time_to_focus)
                    cyclesLeftDescript.visibility = View.VISIBLE
                    timerSettings.visibility = View.INVISIBLE
                }
                Constants.FORCE_START_SEL -> {
                    studyMethodTitle.text = getString(R.string.force_start_title)
                    descriptorinator.text = getString(R.string.jump_start_your_brain)
                    cyclesLeftDescript.visibility = View.INVISIBLE
                    timerSettings.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun initStudyMethodExitBtn() {
        binding.studyMethodExitBtn.setOnClickListener {
            if (JasiriDataHolder.studyActiveStatus.value) {
                Toast.makeText(activity, "Cannot exit while timer is running", Toast.LENGTH_SHORT).show()
            } else {
                parentFragmentManager.beginTransaction().remove(this).commitNow()
            }
        }
    }

    private fun timeStartInit() {
        binding.run {
            timerStartButton.setOnClickListener {
                if (timerStartButton.text == getString(R.string.start)) {
                    startTimer()
                } else {
                    stopTimer()
                }
            }
        }
    }

    private fun timerSettingsInit() {
        binding.timerSettings.setOnClickListener {

        }
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                JasiriDataHolder.setStudyIsActiveStatus(false)
                binding.timerStartButton.text = getString(R.string.start)
                mTimeLeftMillis = Constants.FORCE_START_TIME_MS
                updateCountDownText()
            }
        }.start()
        JasiriDataHolder.setStudyIsActiveStatus(true)
        Log.d(TAG, "timer started")
        binding.timerStartButton.text = getString(R.string.stop)
    }

    private fun stopTimer() {
        mCountDownTimer.cancel()    //pause timer
        timerStopAlertDialogInit()
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftMillis/1000).toInt() / 60
        val seconds = (mTimeLeftMillis/1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.studyTimer.text = timeLeftFormatted
    }

    private fun timerStopAlertDialogInit() {
        builder = AlertDialog.Builder(activity)
        builder!!.setMessage("Are you sure you wanna stop?")
            .setTitle(R.string.bad_cop_1)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                JasiriDataHolder.setStudyIsActiveStatus(false)
                Log.d(TAG, "timer stopped")
                binding.timerStartButton.text = getString(R.string.start)
                mTimeLeftMillis = Constants.FORCE_START_TIME_MS
                updateCountDownText()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                startTimer()
            })
        builder!!.create().show()
    }

    private fun testbtnInit() {
        binding.forceStarTester.setOnClickListener {
            controller.sendMessage("A")
        }
    }


}