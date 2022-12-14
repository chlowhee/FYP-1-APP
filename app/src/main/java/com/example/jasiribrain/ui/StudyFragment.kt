package com.example.jasiribrain.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.jasiribrain.R
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.data.JasiriViewModel
import com.example.jasiribrain.databinding.FragmentStudyBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StudyFragment: Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!
    private val TAG = "studyFragment"

//    private val studyMethodFrag = StudyMethodFrag()

    @Inject lateinit var controller: BluetoothController
    private val viewModel: JasiriViewModel by viewModels()

    private lateinit var mCountDownTimer: CountDownTimer
    private var mTimeLeftMillis = Constants.FORCE_START_TIME_MS
    var builder: AlertDialog.Builder? = null

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

        studyMethodUiInit()
        timeStartInit()
        testbtnInit()
        timerSettingsInit()
    }

    private fun studyMethodCheck() {
        binding.run {
            pomodoroTitleUnselected.setOnClickListener {
                JasiriDataHolder.setStudyMethodSelect(Constants.POMODORO_SEL)
//                studyFragOpener(studyMethodFrag)
            }
//            gtdBtn.setOnClickListener {
//                JasiriDataHolder.setStudyMethodSelect(Constants.GTD_SEL)
//                studyFragOpener(studyMethodFrag)
//            }
            forceStartTitleUnselected.setOnClickListener {
                JasiriDataHolder.setStudyMethodSelect(Constants.FORCE_START_SEL)
//                studyFragOpener(studyMethodFrag)
            }
        }
    }

//    private fun studyFragOpener(fragment: Fragment) {
//        childFragmentManager.beginTransaction()
//            .replace(R.id.methodFragment, fragment)
//            .commitNow()
//    }

    private fun studyMethodUiInit() {
        viewModel.studyMethodStatus.observe(viewLifecycleOwner) { sel ->
            binding.run {
                when (sel) {
                    Constants.POMODORO_SEL -> {
                        pomodoroTitleSelected.visibility = View.VISIBLE
                        pomodoroTitleUnselected.visibility = View.INVISIBLE
                        forceStartTitleSelected.visibility = View.INVISIBLE
                        forceStartTitleUnselected.visibility = View.VISIBLE

                        descriptorinator.text = getString(R.string.time_to_focus)
                        cyclesLeftDescript.visibility = View.VISIBLE
                        timerSettings.visibility = View.VISIBLE

                        mTimeLeftMillis = Constants.POMODORO_DEFAULT_TIME_MS
                        studyTimer.text = "15:00"   //mutable
                    }
                    Constants.FORCE_START_SEL -> {
                        forceStartTitleSelected.visibility = View.VISIBLE
                        forceStartTitleUnselected.visibility = View.INVISIBLE
                        pomodoroTitleSelected.visibility = View.INVISIBLE
                        pomodoroTitleUnselected.visibility = View.VISIBLE

                        descriptorinator.text = getString(R.string.jump_start_your_brain)
                        cyclesLeftDescript.visibility = View.INVISIBLE
                        timerSettings.visibility = View.INVISIBLE

                        mTimeLeftMillis = Constants.FORCE_START_TIME_MS
                        studyTimer.text = "02:00"
                    }
                }
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

    /**
     * Timer functions
     */

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
                val timerStopRing: MediaPlayer = MediaPlayer.create(activity, R.raw.timer_stop_ring)
                timerStopRing.start()
                updateCountDownText() //TODO: Goto a func that changes btwn break and study
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

    /**
     * POMODORO FUNCTIONS
     */
    private fun timerSettingsInit() {
        binding.timerSettings.setOnClickListener {

        }
    }

    private fun testbtnInit() {
        binding.forceStarTester.setOnClickListener {
            controller.sendMessage(Constants.FWD)
        }
    }

}