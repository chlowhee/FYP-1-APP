package com.example.jasiriheart.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.jasiriheart.R
import com.example.jasiriheart.bluetooth.BTDeviceFragment
import com.example.jasiriheart.databinding.ActivityDynamicBinding

class DynamicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDynamicBinding
    private val btFragment = BTDeviceFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDynamicBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction().replace(R.id.container_dynamic, btFragment).commit()
    }

}