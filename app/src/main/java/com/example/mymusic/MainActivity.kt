package com.example.mymusic

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mymusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(SongsFragment())
        Log.d(Short.toString(), "MainActivity onCreate here!!")

        binding.bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.songs -> replaceFragment(SongsFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.library -> replaceFragment(LibraryFragment())

                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}