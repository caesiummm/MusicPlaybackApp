package com.example.mymusic

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mymusic.databinding.ActivityMainBinding
import kotlin.system.exitProcess

open class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(Short.toString(), "MainActivity onCreate here!!")
        replaceFragment(SongsFragment())

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

    open fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        Log.d("replaceFragment", fragmentManager.toString())

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_nav, fragment)
        fragmentTransaction.commit()
    }

    // Media player is not null after calling release(). Hence, it will not be reinitialized when this activity reopens
    // IllegalStateException will be thrown on reset() if media player is not set to null when closing the activity
    override fun onDestroy() {
        super.onDestroy()
        if (SongPlaybackActivity.playbackService!!.mediaPlayer == null) exitProcess(1)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}
