package com.example.jasiribrain.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object JasiriDataHolder {

    /**
     * monitor if timer in StudyFragment is running
     */
    private val _timerActiveStatus = MutableStateFlow(false)

    val timerActiveStatus: StateFlow<Boolean>
        get() = _timerActiveStatus

    fun setTimerIsActiveStatus(status: Boolean) {
        _timerActiveStatus.value = status
    }

    /**
     * monitor which method is chosen
     */
    private val _studyMethodSelect = MutableStateFlow(Constants.FORCE_START_SEL)

    val studyMethodSelect: StateFlow<Int>
        get() = _studyMethodSelect

    fun setStudyMethodSelect(status: Int) {
        _studyMethodSelect.value = status
    }

    /**
     * monitor pomodoro duration chosen
     */
    private val _pomodoroDuration = MutableStateFlow(25)

    val pomodoroDuration: StateFlow<Int>
        get() = _pomodoroDuration

    fun setPomodoroDuration(status: Int) {
        _pomodoroDuration.value = status
    }

    /**
     * monitor break duration chosen
     */
    private val _breakDuration = MutableStateFlow(5)

    val breakDuration: StateFlow<Int>
        get() = _breakDuration

    fun setBreakDuration(status: Int) {
        _breakDuration.value = status
    }

    /**
     * monitor number of cycles chosen
     */
    // To retain originally set value
    private val _numCyclesSet = MutableStateFlow(1)

    val numCyclesSet: StateFlow<Int>
        get() = _numCyclesSet

    fun setNumCycles(status: Int) {
        _numCyclesSet.value = status
    }

    // To count down number of cycles left
    private val _numCyclesCounter = MutableStateFlow(1)

    val numCyclesCounter: StateFlow<Int>
        get() = _numCyclesCounter

    fun setNumCyclesCounter(status: Int) {
        _numCyclesCounter.value = status
    }

    /**
     * monitor if it is break time during pomodoro
     */
    private val _isPomodoroBreak = MutableStateFlow(false)

    val isPomodoroBreak: StateFlow<Boolean>
        get() = _isPomodoroBreak

    fun setIsPomodoroBreak(status: Boolean) {
        _isPomodoroBreak.value = status
    }

    /**
     * monitor if bluetooth is connected to Jasiri
     */
    private val _bluetoothActiveStatus = MutableStateFlow(false)

    val bluetoothActiveStatus: StateFlow<Boolean>
        get() = _bluetoothActiveStatus

    fun setBluetoothIsActiveStatus(status: Boolean) {
        _bluetoothActiveStatus.value = status
    }

    /**
     * monitor joystick cmd
     */
    private val _joystickCmdStatus = MutableStateFlow("default")

    val joystickCmdStatus: StateFlow<String>
        get() = _joystickCmdStatus

    fun setJoystickCmdToSend(status: String) {
        _joystickCmdStatus.value = status
    }

    /**
     * check if RPi is ready to accept nxt cmd
     */
    private val _rpiReadyStatus = MutableStateFlow(false)

    val rpiReadyStatus: StateFlow<Boolean>
        get() = _rpiReadyStatus

    fun setRpiIsReadyStatus(status: Boolean) {
        _rpiReadyStatus.value = status
    }

    /**
     *  toggle if eye detection is wanted
     */
    private val _eyeDetectionToggle = MutableStateFlow(false)

    val eyeDetectionToggle: StateFlow<Boolean>
        get() = _eyeDetectionToggle

    fun setEyeDetectionToggle(status: Boolean) {
        _eyeDetectionToggle.value = status
    }

    /**
     * to turn on and off eye detection
     */
    private val _eyeDetectionIsWanted = MutableStateFlow(false)

    val eyeDetectionIsWanted: StateFlow<Boolean>
        get() = _eyeDetectionIsWanted

    fun setEyeDetectionIsWanted(status: Boolean) {
        _eyeDetectionIsWanted.value = status
    }

    /**
     * to check if eye is sleepy
     */
    private val _eyesAreSleepy = MutableStateFlow(false)

    val eyesAreSleepy: StateFlow<Boolean>
        get() = _eyesAreSleepy

    fun setEyesAreSleepy(status: Boolean) {
        _eyesAreSleepy.value = status
    }

    /**
     * toggle if face tracking is wanted
     */
    private val _faceTrackingIsWanted = MutableStateFlow(false)

    val faceTrackingIsWanted: StateFlow<Boolean>
        get() = _faceTrackingIsWanted

    fun setFaceTrackingIsWanted(status: Boolean) {
        _faceTrackingIsWanted.value = status
    }

    /**
     *  for face tracking
     */
    private val _facePosition = MutableStateFlow(0)

    val facePosition: StateFlow<Int>
        get() = _facePosition

    fun setFacePosition(status: Int) {
        _facePosition.value = status
    }

    /**
     *  monitor if jasiri button is pressed for reaction time game
     */
    private val _hasUserReacted = MutableStateFlow(false)

    val hasUserReacted: StateFlow<Boolean>
        get() = _hasUserReacted

    fun setHasuserReacted(status: Boolean) {
        _hasUserReacted.value = status
    }

    /**
     *  update reaction timing
     */
    private val _reactionTiming = MutableStateFlow("0")

    val reactionTiming: StateFlow<String>
        get() = _reactionTiming

    fun setReactionTiming(status: String) {
        _reactionTiming.value = status
    }

}