package com.example.jasiribrain.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object JasiriDataHolder {

    /**
     * monitor study mode timer running
     */
    private val _studyActiveStatus = MutableStateFlow(false)

    val studyActiveStatus: StateFlow<Boolean>
        get() = _studyActiveStatus

    fun setStudyIsActiveStatus(status: Boolean) {
        _studyActiveStatus.value = status
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
    private val _numCyclesSet = MutableStateFlow(1)

    val numCyclesSet: StateFlow<Int>
        get() = _numCyclesSet

    fun setNumCycles(status: Int) {
        _numCyclesSet.value = status
    }

    // numCyclesSet for retaining originally set value. numCyclesCounter for counting down
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
     * monitor bluetooth (not in use)
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
     * rpiReadyStatus: check if RPi is ready to accept nxt cmd
     */
    private val _rpiReadyStatus = MutableStateFlow(false)

    val rpiReadyStatus: StateFlow<Boolean>
        get() = _rpiReadyStatus

    fun setRpiIsReadyStatus(status: Boolean) {
        _rpiReadyStatus.value = status
    }

}