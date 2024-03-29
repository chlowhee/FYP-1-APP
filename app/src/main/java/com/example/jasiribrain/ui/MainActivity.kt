package com.example.jasiribrain.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.data.JasiriDataHolder
import com.example.jasiribrain.data.JasiriViewModel
import com.example.jasiribrain.databinding.ActivityMainBinding
import com.example.jasiribrain.facedetector.CameraPreview
import com.example.jasiribrain.utils.getMissingPermissions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
private val REQUIRED_PERMISSION_LIST = arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.CAMERA
)
private const val REQUEST_PERMISSION_CODE = 12345

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val settingsFrag = SettingsFragment()
    private val studyFrag = StudyFragment()
    private val homeFrag = HomeFragment()
    private lateinit var currFrag: Fragment
    private val cameraPreview = CameraPreview()

    private val viewModel: JasiriViewModel by viewModels()
    @Inject lateinit var controller: BluetoothController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.bottomNavigationView.menu.findItem(R.id.settings).isChecked = true
        supportFragmentManager.beginTransaction().replace(R.id.settings_fragment, settingsFrag).commit()


        checkAndRequestPermissions()
        bottomNavSelect()
        bottomNavEnabled()
        toggleFaceDetection()
        faceTrackerMover()
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
            if (!JasiriDataHolder.timerActiveStatus.value) {
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

    /**
     * FACIAL DETECTION
     */
    fun activateFaceDetection() {
        if (!cameraPreview.isAdded) {
            supportFragmentManager.beginTransaction().replace(R.id.camera_preview, cameraPreview)
                .commit()
        }
    }

    fun stopFaceDetection() {
        if (cameraPreview.isAdded) {
            supportFragmentManager.beginTransaction().remove(cameraPreview).commit()
        }
    }

    private fun toggleFaceDetection() {
        viewModel.checkEyeDetectionStatus.observe(this) { turnOn ->
            if (turnOn) {
                activateFaceDetection()
                Log.d("LogTagForTest", "toggle ON face detection")
            } else {
                stopFaceDetection()
                Log.d("LogTagForTest", "toggle OFF face detection")
            }
        }
        viewModel.checkFaceTrackingStatus.observe(this) { turnOn ->
            if (turnOn) {
                activateFaceDetection()
            } else if (!turnOn && !JasiriDataHolder.eyeDetectionIsWanted.value) {
                stopFaceDetection()
            }
        }
    }

    /**
     * FACE TRACKER
     */
    private fun faceTrackerMover() {
        viewModel.checkFacePositionStatus.observe(this) { facePos ->
            when (facePos) {
                0 -> return@observe
                1 -> controller.sendMessage("FFF")
                2 -> controller.sendMessage("FF")
                3 -> controller.sendMessage("F")
                4 -> controller.sendMessage("E")
                5 -> controller.sendMessage("EE")
                6 -> controller.sendMessage("EEE")
                7 -> controller.sendMessage("I")
            }
            Log.d("LogTagForTest", "FACE tracker cmd sent")
            JasiriDataHolder.setFacePosition(0)
        }
    }
}

