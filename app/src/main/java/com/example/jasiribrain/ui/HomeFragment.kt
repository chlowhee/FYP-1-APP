package com.example.jasiribrain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.String
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var controller: BluetoothController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joystickControllerInit()
    }

    private fun joystickControllerInit() {
        binding.joystickCtrl.setOnMoveListener { angle, strength ->
            binding.textViewAngleRight.text = "$angle°"
            binding.textViewStrengthRight.text = "$strength%"
            binding.textViewCoordinateRight.text = jasiriMove(angle, strength)//"dir"
//                String.format(
//                "x%03d:y%03d",
//                binding.joystickCtrl.normalizedX,
//                binding.joystickCtrl.normalizedY
//            )
        }
    }

    private fun jasiriMove(angle: Int, strength: Int):kotlin.String {
        //350-10 right 90   11-79: right 45
        //80-100 fwd        101 - 169 left 45
        //170-190 left 90  191 - 259 backleft
        //260-280 bwd      281 - 349 back right
        var dir = "dir"
        if (strength == 0) {    //so wun send R when re-centre
            return dir
        }
        if (angle in 11 until 80) { //fwd R 45
            dir = "fwdR45"
            return dir
        }
        if (angle in 80 until 101) {
            dir = "fwd"
            controller.sendMessage(Constants.FWD)
            return dir
        }
        if (angle in 101 until 170) {
            dir = "fwdL45"
            return dir
        }
        if (angle in 170 until 191) {
            dir = "L"
            return dir
        }
        if (angle in 191 until 260) {
            dir = "bwdL45"
            return dir
        }
        if (angle in 260 until 281) {
            dir = "bwd"
            return dir
        }
        if (angle in 281 until 350) {
            dir = "bwdR45"
            return dir
        }
        if (angle in 350 until 360 || angle in 0 until 11) {
            dir = "R"
            return dir
        }
        return dir
    }

}