package com.example.mymusic

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaybackViewModel: ViewModel() {
    private val _bottomFragmentToAdd = MutableLiveData<Fragment>()
    val bottomFragmentToAdd: LiveData<Fragment> = _bottomFragmentToAdd

    fun addBottomPlaybackFragment(fragment: Fragment) {
        Log.d("PlaybackViewModel", "Passing $fragment")
        _bottomFragmentToAdd.value = fragment
    }
}