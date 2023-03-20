package com.example.jasiribrain.ui

import android.media.MediaPlayer
import android.os.Bundle
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
import com.example.jasiribrain.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val TAG = "homeFrag"

    private val gameFragment = GameFragment()
    private val viewModel: JasiriViewModel by viewModels()
    @Inject lateinit var controller: BluetoothController

    private var prevCmd = Constants.DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        freeMotivationInit()
        playAndDanceButtonInit()
        joystickControllerInit()
        jasiriMove()
    }

    private fun freeMotivationInit() {
        with(binding) {
            goodCopButton.setOnClickListener {
                controller.sendMessage(Constants.GOOD_COP)
                val goodCopVoice: MediaPlayer = MediaPlayer.create(activity, R.raw.good_cop_audio)
                goodCopVoice.start()
            }

            badCopButton.setOnClickListener {
                controller.sendMessage(Constants.BAD_COP)
                val badCopVoice: MediaPlayer = MediaPlayer.create(activity, R.raw.bad_cop_audio)
                badCopVoice.start()
            }
        }
    }

    private fun playAndDanceButtonInit() {
        binding.playButton.setOnClickListener {
            Log.d(TAG, "Play Button pressed")
            if (JasiriDataHolder.bluetoothActiveStatus.value) {
                childFragmentManager.beginTransaction()
                    .replace(R.id.methodFragment_home, gameFragment).commitNow()
            } else {
                Toast.makeText(activity, "Jasiri is not connected!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.danceButton.setOnClickListener {
            Log.d(TAG, "Dance Button pressed")
            controller.sendMessage(Constants.DANCE)
        }
    }

    private fun joystickControllerInit() {
        binding.joystickCtrl.setOnMoveListener({ angle, strength ->
            joystickUpdate(angle, strength)
        }, 30)
    }

    private fun joystickUpdate(angle: Int, strength: Int):String {
        var dir = "dir"
        if (strength < 30) {    //so wun send cmd when re-centre
            prevCmd = Constants.DEFAULT
            return dir
        }
        when (angle) {
            in 45..134 -> {
                dir = "fwd"
                JasiriDataHolder.setJoystickCmdToSend(Constants.FWD)
            }
            in 135..224 -> {
                dir = "L"
                JasiriDataHolder.setJoystickCmdToSend(Constants.LEFT)
            }
            in 225..314 -> {
                dir = "bwd"
                JasiriDataHolder.setJoystickCmdToSend(Constants.BWD)
            }
            in 315..359, in 0..44 -> {
                dir = "R"
                JasiriDataHolder.setJoystickCmdToSend(Constants.RIGHT)
            }
        }
        return dir
    }

    private fun jasiriMove() {
        viewModel.getjoystickCmdStatus.observe(viewLifecycleOwner) { cmd ->
            Log.d(TAG, "RPI ready: " + JasiriDataHolder.rpiReadyStatus.value)
            if (cmd != Constants.DEFAULT && JasiriDataHolder.rpiReadyStatus.value) {
                controller.sendMessage(cmd)
                prevCmd = cmd
                Log.d(TAG, "cmd sent: $cmd")
            }

            JasiriDataHolder.setJoystickCmdToSend(Constants.DEFAULT)
            Log.d(TAG, "cmd set to default")

        }
    }
}