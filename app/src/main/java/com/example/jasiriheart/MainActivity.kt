package com.example.jasiriheart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.jasiriheart.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val settingsFrag = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.bottomNavigationView.menu.findItem(R.id.settings).isChecked = true
        setCurrentFragment(settingsFrag)

    }

    private fun setCurrentFragment(fragment: Fragment) {
        val fragMgr = supportFragmentManager

        fragMgr.beginTransaction().apply{
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

    private fun bottomNavSelect() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
//                R.id.study -> setCurrentFragment(firstFragment)
//                R.id.home -> setCurrentFragment(secondFragment)
                R.id.settings -> setCurrentFragment(settingsFrag)

            }
            true
        }
    }

}

