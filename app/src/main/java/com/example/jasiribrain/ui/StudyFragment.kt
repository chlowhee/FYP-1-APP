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

    private val pomodoroSettingDialog = PomodoroSettingDialog()
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
        pomodoroSettingsInit()
        pomoSettingsSet()
        cyclesLeftSet()
    }

    private fun studyMethodCheck() {
        viewModel.isStudyingStatus.observe(viewLifecycleOwner) { yes ->
            if (yes) {
                binding.pomodoroTitleUnselected.isClickable = false
                binding.forceStartTitleUnselected.isClickable = false
            } else {
                with (binding) {
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

                        cyclesLeftDescript.visibility = View.VISIBLE
                        timerSettings.visibility = View.VISIBLE
                    }
                    Constants.FORCE_START_SEL -> {
                        forceStartTitleSelected.visibility = View.VISIBLE
                        forceStartTitleUnselected.visibility = View.INVISIBLE
                        pomodoroTitleSelected.visibility = View.INVISIBLE
                        pomodoroTitleUnselected.visibility = View.VISIBLE

                        descriptorinator.text = getString(R.string.jump_start_your_brain)
                        cyclesLeftDescript.visibility = View.INVISIBLE
                        timerSettings.visibility = View.INVISIBLE
                    }
                }
                displayTimerInit()
            }
        }
    }

    private fun timeStartInit() {
        binding.run {
            timerStartButton.setOnClickListener {
                if (timerStartButton.text == getString(R.string.start)) {
                    if (JasiriDataHolder.numCyclesCounter.value == 0) {
                        cyclesZeroAlertDialogInit()
                    } else {
                        startTimer()
                    }
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
                if (binding.studyTimer.text == "10:01" || binding.studyTimer.text == "05:01") {
                    Log.d(TAG, "${mTimeLeftMillis/Constants.MINUTE_IN_MILLIS} minutes left")
                    timerMonitorer(mTimeLeftMillis/Constants.MINUTE_IN_MILLIS)
                }
                updateCountDownText()
            }

            override fun onFinish() {
                JasiriDataHolder.setStudyIsActiveStatus(false)
                binding.timerStartButton.text = getString(R.string.start)
                val timerStopRing: MediaPlayer = MediaPlayer.create(activity, R.raw.timer_stop_ring)
                timerStopRing.start()
                if (JasiriDataHolder.studyMethodSelect.value == Constants.POMODORO_SEL) {
                    timerEndAction()
                    JasiriDataHolder.setIsPomodoroBreak(!JasiriDataHolder.isPomodoroBreak.value)
                }
                displayTimerInit()
            }
        }.start()
        JasiriDataHolder.setStudyIsActiveStatus(true)
        Log.d(TAG, "timer started")
        binding.timerStartButton.text = getString(R.string.stop)
    }

    private fun stopTimer() {
        mCountDownTimer.cancel()    //pause timer
        if (!JasiriDataHolder.isPomodoroBreak.value) {
            timerStopAlertDialogInit()
        } else {
            JasiriDataHolder.setNumCyclesCounter(JasiriDataHolder.numCyclesCounter.value-1)
            JasiriDataHolder.setIsPomodoroBreak(!JasiriDataHolder.isPomodoroBreak.value)
            displayTimerInit()
            JasiriDataHolder.setStudyIsActiveStatus(false)
            Log.d(TAG, "timer stopped")
            binding.timerStartButton.text = getString(R.string.start)
        }
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftMillis/1000).toInt() / 60
        val seconds = (mTimeLeftMillis/1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.studyTimer.text = timeLeftFormatted
    }

    private fun timerStopAlertDialogInit() {
        builder = AlertDialog.Builder(activity)
        builder!!.setMessage("Are you sure you wanna stop? It will mean you have not completed a cycle.")
            .setTitle(R.string.bad_cop_1)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                JasiriDataHolder.setStudyIsActiveStatus(false)
                Log.d(TAG, "timer stopped")
                binding.timerStartButton.text = getString(R.string.start)
//                JasiriDataHolder.setIsPomodoroBreak(!JasiriDataHolder.isPomodoroBreak.value)
                displayTimerInit()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                startTimer()
            })
        builder!!.create().show()
    }

    private fun displayTimerInit() {
        if (JasiriDataHolder.studyMethodSelect.value != 1) {
            mTimeLeftMillis = Constants.FORCE_START_TIME_MS
            updateCountDownText()
            return
        }
        with(binding) {
            if (!JasiriDataHolder.isPomodoroBreak.value) {
                val pomodoroTimeSet = JasiriDataHolder.pomodoroDuration.value
                descriptorinator.text = getString(R.string.time_to_focus)
                mTimeLeftMillis = (pomodoroTimeSet * Constants.MINUTE_IN_MILLIS)
            } else {
                val breakTimeSet = JasiriDataHolder.breakDuration.value
                descriptorinator.text = getString(R.string.time_for_a_break_xd)
                mTimeLeftMillis = (breakTimeSet * Constants.MINUTE_IN_MILLIS)
            }
        }
        updateCountDownText()
    }

    /**
     * POMODORO FUNCTIONS
     */

    private fun pomodoroSettingsInit() {
        binding.timerSettings.setOnClickListener {
            if (!pomodoroSettingDialog.isAdded && !JasiriDataHolder.studyActiveStatus.value) {
                pomodoroSettingDialog.show(childFragmentManager, pomodoroSettingDialog.TAG)
            }
        }
    }

    // to set the timer after confirming in PomodoroSettingsDialog
    private fun pomoSettingsSet() {
        viewModel.pomodoroDurationStatus.observe(viewLifecycleOwner) {
            Log.d(TAG, "pomoSettings Set")
            displayTimerInit()
        }
    }

    private fun cyclesLeftSet() {
        viewModel.numCyclesStatus.observe(viewLifecycleOwner) {
                binding.cyclesLeftDescript.text = getString(R.string.cycles_left, it)
        }
    }

    private fun cyclesZeroAlertDialogInit() {
        builder = AlertDialog.Builder(activity)
        builder!!.setMessage("If you choose to continue, your cycles will be reset")
            .setTitle("You have completed your set cycles!")
            .setPositiveButton("Reset") { dialog, id ->
                JasiriDataHolder.setNumCyclesCounter(JasiriDataHolder.numCyclesSet.value)
            }
            .setNegativeButton("No") { dialog, id -> }
        builder!!.create().show()
    }

    /**
     * ROBOT COMMAND FUNCTIONS
     */

    //10 min -> fidget
    //5 min -> reminder to keep up
    //timer stop -> ring and do congratulatory motion
    
    private fun timerMonitorer(time: Long) {
        if (JasiriDataHolder.isPomodoroBreak.value) {return}
        when (time) {
            10L -> {
                Log.d(TAG, "ROBOT FIDGET")
                val tenMinReminder: MediaPlayer = MediaPlayer.create(activity, R.raw.ten_mins_left)
                tenMinReminder.start()
                controller.sendMessage(Constants.FIDGET)
            }
            5L -> {
                Log.d(TAG, "ROBOT DANCE")
                val fiveMinReminder: MediaPlayer = MediaPlayer.create(activity, R.raw.five_mins_left)
                fiveMinReminder.start()
                controller.sendMessage(Constants.FIDGET)
            }
        }
    }

    private fun timerEndAction() {
        //TODO
        if (JasiriDataHolder.isPomodoroBreak.value) {   //after break end
            JasiriDataHolder.setNumCyclesCounter(JasiriDataHolder.numCyclesCounter.value-1)
            Log.d(TAG, "ROBO DANCE AND SAY TIME FOR NEXT CYCLE")
            controller.sendMessage(Constants.DANCE)
        } else {
            Log.d(TAG, "ROBO SAYS CONGRATS")
            val pomodoroEndedAlerter: MediaPlayer = MediaPlayer.create(activity, R.raw.pomodoro_ended)
            pomodoroEndedAlerter.start()
            controller.sendMessage(Constants.FIDGET)

        }
    }

    private fun testbtnInit() {
        binding.forceStarTester.setOnClickListener {
            controller.sendMessage(Constants.FWD)
        }
    }

}