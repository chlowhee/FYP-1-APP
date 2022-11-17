package com.example.jasiribrain.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.databinding.ActivityMainBinding
import com.example.jasiribrain.utils.getMissingPermissions
import dagger.hilt.android.AndroidEntryPoint
import com.example.jasiribrain.data.JasiriDataHolder
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.data.JasiriViewModel

private val REQUIRED_PERMISSION_LIST = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.ACCESS_FINE_LOCATION
)
private const val REQUEST_PERMISSION_CODE = 12345

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val settingsFrag = SettingsFragment()
    private val studyFrag = StudyFragment()
    private val homeFrag = HomeFragment()
    private lateinit var currFrag: Fragment

    private val viewModel: JasiriViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.bottomNavigationView.menu.findItem(R.id.settings).isChecked = true
//        setCurrentFragment(settingsFrag)
        supportFragmentManager.beginTransaction().replace(R.id.settings_fragment, settingsFrag).commit()


        checkAndRequestPermissions()
        bottomNavSelect()
        bottomNavEnabled()
    }

    /**
     * Checks if there is any missing permissions, and
     * requests runtime permission if needed.
     */
    private fun checkAndRequestPermissions() {
        // Check for permissions
        val missingPermission: Array<String> = this.getMissingPermissions(*REQUIRED_PERMISSION_LIST)

        // Request for missing permissions
        if (missingPermission.isNotEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(missingPermission, REQUEST_PERMISSION_CODE)
        }
    }

    /**
     * Result of runtime permission request
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check for granted permission and remove from missing list
        if (requestCode == REQUEST_PERMISSION_CODE) {
            var allGranted = true
            for (i in grantResults.indices.reversed()) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }
        }
    }

    /**
     * Sets the current fragment shown on the MainActivity
     */
    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    /**
     * Bottom Navigation Tool to change fragment
     */
    private fun bottomNavSelect() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            if (JasiriDataHolder.studyActiveStatus.value == false) {
                when (it.itemId) {
                    R.id.study -> {
                        setCurrentFragment(studyFrag)
                        currFrag = studyFrag
                    }
                    R.id.home -> {
                        setCurrentFragment(homeFrag)
                        currFrag = homeFrag
                    }
                    R.id.settings -> supportFragmentManager.beginTransaction().remove(currFrag).commitNow()
                }
            }
            true
        }
    }

    private fun bottomNavEnabled() {
        viewModel.isStudyingStatus.observe(this) { yes ->
            binding.bottomNavigationView.apply {
                if (yes) menu.forEach { it.isEnabled = false }
                else menu.forEach { it.isEnabled = true }
            }
        }
    }

}
