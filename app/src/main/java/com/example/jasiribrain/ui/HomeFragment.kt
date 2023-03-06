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
            binding.textViewAngleRight.text = "$angle°"
            binding.textViewStrengthRight.text = "$strength%"
            binding.textViewCoordinateRight.text = joystickUpdate(angle, strength)
        }, 30)
    }

    private fun joystickUpdate(angle: Int, strength: Int):String {
        //350-10 right 90   11-79: right 45
        //80-100 fwd        101 - 169 left 45
        //170-190 left 90  191 - 259 backleft
        //260-280 bwd      281 - 349 back right
        var dir = "dir"
        if (strength < 30) {    //so wun send R when re-centre
            prevCmd = Constants.DEFAULT
            return dir
        }
        when (angle) {  //TODO: reeval
            in 11..79 -> {
                dir = "fwdR45"
            }
            in 80..100 -> {
                dir = "fwd"
                JasiriDataHolder.setJoystickCmdToSend(Constants.FWD)
            }
            in 101..169 -> {
                dir = "fwdL45"
            }
            in 170..190 -> {
                dir = "L"
                JasiriDataHolder.setJoystickCmdToSend(Constants.LEFT)
            }
            in 191..259 -> {
                dir = "bwdL45"
            }
            in 260..280 -> {
                dir = "bwd"
                JasiriDataHolder.setJoystickCmdToSend(Constants.BWD)
            }
            in 281..349 -> {
                dir = "bwdR45"
            }
            in 350..359, in 0..10 -> {
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