package com.example.jasiribrain.ui

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private val gameFragment = GameFragment()
    private val pomodoroSettingDialog = PomodoroSettingDialog()
    @Inject lateinit var controller: BluetoothController
    private val viewModel: JasiriViewModel by viewModels()

    private lateinit var mCountDownTimer: CountDownTimer
    private var mTimeLeftMillis = Constants.FORCE_START_TIME_MS
    var builder: AlertDialog.Builder? = null
    var toggleEyeD = false

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
        buttonClick()
        timeStartInit()
//        testbtnInit()
//        testbtn2Init()
        pomodoroSettingsInit()
        pomoSettingsSet()
        cyclesLeftSet()
        personSleepyCheck()
        playButtonInitStudy()
        freeMotivationInitStudy()
    }

    /**
     * UI Initializations
     */

    private fun studyMethodCheck() {
        viewModel.isStudyingStatus.observe(viewLifecycleOwner) { yes ->
            if (yes) {
                binding.pomodoroTitleUnselected.isClickable = false
                binding.forceStartTitleUnselected.isClickable = false
            } else {
                with (binding) {
                    pomodoroTitleUnselected.setOnClickListener {
                        JasiriDataHolder.setStudyMethodSelect(Constants.POMODORO_SEL)
                    }
                    forceStartTitleUnselected.setOnClickListener {
                        JasiriDataHolder.setStudyMethodSelect(Constants.FORCE_START_SEL)
                    }
                }
            }
        }
    }

    private fun studyMethodUiInit() {
        viewModel.studyMethodStatus.observe(viewLifecycleOwner) { sel ->
            with (binding) {
                when (sel) {
                    Constants.POMODORO_SEL -> {
                        pomodoroTitleSelected.visibility = View.VISIBLE
                        pomodoroTitleUnselected.visibility = View.INVISIBLE
                        forceStartTitleSelected.visibility = View.INVISIBLE
                        forceStartTitleUnselected.visibility = View.VISIBLE
                        haptic1.visibility = View.VISIBLE
                        haptic2.visibility = View.VISIBLE
                        haptic3.visibility = View.VISIBLE

                        cyclesLeftDescript.visibility = View.VISIBLE
                        timerSettings.visibility = View.VISIBLE
                    }
                    Constants.FORCE_START_SEL -> {
                        forceStartTitleSelected.visibility = View.VISIBLE
                        forceStartTitleUnselected.visibility = View.INVISIBLE
                        pomodoroTitleSelected.visibility = View.INVISIBLE
                        pomodoroTitleUnselected.visibility = View.VISIBLE
                        haptic1.visibility = View.INVISIBLE
                        haptic2.visibility = View.INVISIBLE
                        haptic3.visibility = View.INVISIBLE
                        playButtonBreak.visibility = View.INVISIBLE
                        motivationButtonsBreak.visibility = View.INVISIBLE

                        descriptorinator.text = getString(R.string.jump_start_your_brain)
                        cyclesLeftDescript.visibility = View.INVISIBLE
                        timerSettings.visibility = View.INVISIBLE
                    }
                }
                displayTimerInit()
            }
        }
    }

    private fun buttonClick(){
        with (binding) {
            haptic1.setOnClickListener {
                val buttonClicked: MediaPlayer = MediaPlayer.create(activity, R.raw.button_click)
                buttonClicked.start()
            }
            haptic3.setOnClickListener {
                val buttonClicked: MediaPlayer = MediaPlayer.create(activity, R.raw.button_click)
                buttonClicked.start()
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
                if (binding.studyTimer.text == "10:02" || binding.studyTimer.text == "05:02") {
                    Log.d(TAG, "${mTimeLeftMillis/Constants.MINUTE_IN_MILLIS} minutes left")
                    timerMonitorer(mTimeLeftMillis/Constants.MINUTE_IN_MILLIS)
                }
                updateCountDownText()
            }

            override fun onFinish() {
                JasiriDataHolder.setTimerIsActiveStatus(false)
                binding.timerStartButton.text = getString(R.string.start)
                val timerStopRing: MediaPlayer = MediaPlayer.create(activity, R.raw.timer_stop_ring)
                timerStopRing.start()
                if (JasiriDataHolder.studyMethodSelect.value == Constants.POMODORO_SEL) {
                    timerEndAction()
                    JasiriDataHolder.setIsPomodoroBreak(!JasiriDataHolder.isPomodoroBreak.value)
                    toggleEyeDetectionEveryTwoMins()
                }
                displayTimerInit()
            }
        }.start()
        JasiriDataHolder.setTimerIsActiveStatus(true)
        if (JasiriDataHolder.studyMethodSelect.value == Constants.POMODORO_SEL && !JasiriDataHolder.isPomodoroBreak.value) {
            toggleEyeD = true
            toggleEyeDetectionEveryTwoMins()
        }
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
            JasiriDataHolder.setTimerIsActiveStatus(false)
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
            .setPositiveButton("Yes") { dialog, id ->
                JasiriDataHolder.setTimerIsActiveStatus(false)
                Log.d(TAG, "timer stopped")
                binding.timerStartButton.text = getString(R.string.start)
                displayTimerInit()
                toggleEyeD = false
                toggleEyeDetectionEveryTwoMins()
            }
            .setNegativeButton("No") { dialog, id ->
                startTimer()
            }
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
                playButtonBreak.visibility = View.INVISIBLE
                motivationButtonsBreak.visibility = View.INVISIBLE
                mTimeLeftMillis = (pomodoroTimeSet * Constants.MINUTE_IN_MILLIS)
            } else {
                val breakTimeSet = JasiriDataHolder.breakDuration.value
                descriptorinator.text = getString(R.string.time_for_a_break_xd)
                playButtonBreak.visibility = View.VISIBLE
                motivationButtonsBreak.visibility = View.VISIBLE
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
            if (!pomodoroSettingDialog.isAdded && !JasiriDataHolder.timerActiveStatus.value) {
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
        if (JasiriDataHolder.isPomodoroBreak.value) return
        when (time) {
            10L -> {
                toggleEyeD = false
                toggleEyeDetectionEveryTwoMins()
                requireActivity().runOnUiThread {
                    val tenMinReminder: MediaPlayer = MediaPlayer.create(activity, R.raw.ten_mins_left)
                    tenMinReminder.start()
                }
                controller.sendMessage(Constants.FIDGET)
            }
            5L -> {
                toggleEyeD = false
                toggleEyeDetectionEveryTwoMins()
                requireActivity().runOnUiThread {
                    val fiveMinReminder: MediaPlayer =
                        MediaPlayer.create(activity, R.raw.five_mins_left)
                    fiveMinReminder.start()
                }
                controller.sendMessage(Constants.FIDGET)
            }
        }
    }

    private fun timerEndAction() {
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

    /**
     * EYE DETECTION
     */
    private fun toggleEyeDetectionEveryTwoMins() {
        if (!JasiriDataHolder.eyeDetectionToggle.value) return
        val timerObj = Timer()
        val timerTaskObj = object : TimerTask() {
            override fun run() {
                if (!JasiriDataHolder.isPomodoroBreak.value && JasiriDataHolder.timerActiveStatus.value) {
                    if(toggleEyeD) {
                        JasiriDataHolder.setEyeDetectionIsWanted(true)
                        Log.d("LogTagForTest", "toggle ON eye detection every 2 mins")
                    } else {
                        JasiriDataHolder.setEyeDetectionIsWanted(false)
                        Log.d("LogTagForTest", "toggle OFF eye detection every 2 mins")
                    }
                    toggleEyeD = !toggleEyeD
                } else {
                    JasiriDataHolder.setEyeDetectionIsWanted(false)
                    timerObj.cancel()
                    timerObj.purge()
                    Log.d("LogTagForTest", "toggle PURGE eye detection every 2 mins")
                }
            }
        }
        timerObj.schedule(timerTaskObj, 0, 2*Constants.MINUTE_IN_MILLIS)
    }

    private fun personSleepyCheck() {
        viewModel.checkEyeIsSleepyStatus.observe(viewLifecycleOwner) {sleepy ->
            if (sleepy) {
                controller.sendMessage(Constants.FIDGET)
                Log.d("LogTagForTest", "EYE SLEEPY cmd sent")
                JasiriDataHolder.setEyesAreSleepy(false)
            }
        }
    }

    /**
     *  Pomodoro break extras
     */

    private fun playButtonInitStudy() {
        binding.playButtonBreak.setOnClickListener {
            Log.d(TAG, "Play Button pressed")
            if (JasiriDataHolder.bluetoothActiveStatus.value) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.methodFragment_study, gameFragment).commitNow()
            } else {
                Toast.makeText(activity, "Jasiri is not connected!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun freeMotivationInitStudy() {
        with (binding) {
            goodCopButtonBreak.setOnClickListener {
                controller.sendMessage(Constants.GOOD_COP)
                val goodCopVoice: MediaPlayer = MediaPlayer.create(activity, R.raw.good_cop_audio)
                goodCopVoice.start()
            }

            badCopButtonBreak.setOnClickListener {
                controller.sendMessage(Constants.BAD_COP)
                val badCopVoice: MediaPlayer = MediaPlayer.create(activity, R.raw.bad_cop_audio)
                badCopVoice.start()
            }
        }
    }

//    private fun testbtnInit() {
//        binding.forceStarTester.setOnClickListener {
////            controller.sendMessage(Constants.FWD)
//            (activity as MainActivity).activateFaceDetection()
////            toggleEyeDetectionEveryFiveMins()
//            JasiriDataHolder.setFaceTrackingIsWanted(true)
//        }
//    }
//
//    private fun testbtn2Init() {
//        binding.forceStarTester2.setOnClickListener {
////            (activity as MainActivity).stopFaceDetection()
//            JasiriDataHolder.setFaceTrackingIsWanted(false)
//        }
//    }

}