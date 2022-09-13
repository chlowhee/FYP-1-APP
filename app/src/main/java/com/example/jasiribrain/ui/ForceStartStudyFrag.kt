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
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.databinding.FragStudyForceStartBinding
import java.util.*

class ForceStartStudyFrag: Fragment() {

    private var _binding: FragStudyForceStartBinding? = null
    private val binding get() = _binding!!
    private val TAG = "forceStartStudyFrag"

    private val controller = BluetoothController(activity)

    private lateinit var mCountDownTimer: CountDownTimer
    private var mTimeLeftMillis = Constants.FORCE_START_TIME_MS
    var builder: AlertDialog.Builder? = null

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
        timeStartInit()
        testbtnInit()
    }

    private fun initForceStartExitBtn() {
        binding.forceStartExitBtn.setOnClickListener {
            Log.d(TAG, "exitBtnClicked. studyIsActive: " + JasiriDataHolder.studyActiveStatus.value)
            if (JasiriDataHolder.studyActiveStatus.value) {
                Toast.makeText(activity, "Cannot exit while timer is running", Toast.LENGTH_LONG).show()
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
        Log.d(TAG, "timer started. studyIsActive: " + JasiriDataHolder.studyActiveStatus)
        binding.timerStartButton.text = getString(R.string.stop)
    }

    private fun stopTimer() {
        mCountDownTimer.cancel()    //pause timer
        stopAlertDialogInit()
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftMillis/1000).toInt() / 60
        val seconds = (mTimeLeftMillis/1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.forceStartTimer.text = timeLeftFormatted
    }

    private fun stopAlertDialogInit() {
        builder = AlertDialog.Builder(activity)
        builder!!.setMessage("Are you sure you wanna stop?")
            .setTitle(R.string.bad_cop_1)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                JasiriDataHolder.setStudyIsActiveStatus(false)
                Log.d(TAG, "timer stopped. studyIsActive: " + JasiriDataHolder.studyActiveStatus.value)
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