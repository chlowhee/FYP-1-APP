package com.example.jasiriheart.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.jasiriheart.R
import com.example.jasiriheart.data.Constants
import com.example.jasiriheart.databinding.ActivityMainBinding
import com.example.jasiriheart.utils.getMissingPermissions
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.bottomNavigationView.menu.findItem(R.id.settings).isChecked = true
        setCurrentFragment(settingsFrag)

        checkAndRequestPermissions()
        bottomNavSelect()
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
            when (it.itemId) {
                R.id.study -> setCurrentFragment(studyFrag)
                R.id.home -> setCurrentFragment(homeFrag)
                R.id.settings -> setCurrentFragment(settingsFrag)

            }
            true
        }
    }

}

