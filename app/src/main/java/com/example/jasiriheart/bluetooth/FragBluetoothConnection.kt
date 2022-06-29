package com.example.jasiriheart.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.jasiriheart.databinding.BluetoothConnectFragBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragBluetoothConnection: Fragment() {

    private var _binding: BluetoothConnectFragBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BluetoothConnectFragBinding.inflate(inflater, container, false)
        return binding.root
    }

}