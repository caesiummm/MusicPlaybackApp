package com.example.mymusic.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.mymusic.adapter.LibraryViewPagerAdapter
import com.example.mymusic.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayout

class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var libraryViewPagerAdapter: LibraryViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        libraryViewPagerAdapter = LibraryViewPagerAdapter(parentFragmentManager, lifecycle)

        binding.tabLibrary.addTab(binding.tabLibrary.newTab().setText("Liked Music"))
        binding.tabLibrary.addTab(binding.tabLibrary.newTab().setText("Playlist"))

        binding.viewPagerLibrary.adapter = libraryViewPagerAdapter

        binding.tabLibrary.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPagerLibrary.currentItem = tab.position
                    Log.d("Tab Layout", tab.position.toString())
                } else {
                    Log.d("Tab Layout", "Tab is NULL!!!")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewPagerLibrary.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLibrary.selectTab(binding.tabLibrary.getTabAt(position))
            }
        })
    }
}