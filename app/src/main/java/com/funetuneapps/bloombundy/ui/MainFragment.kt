package com.funetuneapps.bloombundy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.classes.Constants
import com.funetuneapps.bloombundy.classes.Constants.myToast
import com.funetuneapps.bloombundy.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val homeFrag = HomeFragment()
    private val chatFrag = ChatFragment()
    private val feedFrag = FeedFragment()
    private val profileFrag = ProfileFragment()
    private lateinit var currentFragment: Fragment
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentMainBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsFirstLoad) {
            initialFragmentTransaction()
            launchPermission()
            Constants.mainFragsSwitch = {
                when (it) {
                    1 -> {
                        goToFragment(homeFrag, R.id.home)
                    }

                    2 -> {
                        goToFragment(chatFrag, R.id.inbox)
                    }
                }
            }
        }

        binding.addPlantBtn.setOnClickListener {
            if (findNavController().currentDestination?.id==R.id.mainFragment){
                findNavController().navigate(R.id.action_mainFragment_to_addPlantFragment)
            }
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentFragment != homeFrag) {
                    goToFragment(homeFrag, R.id.home)
                } else {
                    requireActivity().finishAffinity()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)

    }

    private fun initialFragmentTransaction() {
        val fm = childFragmentManager
        try {
            fm.findFragmentByTag("3")?.let {
                fm.beginTransaction().remove(it).commit()
            }
            fm.findFragmentByTag("2")?.let {
                fm.beginTransaction().remove(it).commit()
            }
            fm.findFragmentByTag("1")?.let {
                fm.beginTransaction().remove(it).commit()
            }
            fm.findFragmentByTag("0")?.let {
                fm.beginTransaction().remove(it).commit()
            }
        } catch (_: java.lang.Exception) {
        } catch (_: Exception) {
        }
        fm.beginTransaction().add(binding.fragmentContainer.id, profileFrag, "3").commit()
        fm.beginTransaction().hide(profileFrag).commit()
        fm.beginTransaction().add(binding.fragmentContainer.id, feedFrag, "2").commit()
        fm.beginTransaction().hide(feedFrag).commit()
        fm.beginTransaction().add(binding.fragmentContainer.id, chatFrag, "1").commit()
        fm.beginTransaction().hide(chatFrag).commit()
        fm.beginTransaction().add(binding.fragmentContainer.id, homeFrag, "0").commit()
        currentFragment = homeFrag
        lifecycleScope.launch(Dispatchers.Main) {
            kotlin.runCatching {
                delay(100)
                binding.bottomNavigation.selectedItemId = R.id.home
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    goToFragment(homeFrag)
                    binding.toolbar.text = "Home"
                }

                R.id.inbox -> {
                    goToFragment(chatFrag)
                    binding.toolbar.text = "Chats"

                }

                R.id.order -> {
                    goToFragment(feedFrag)
                    binding.toolbar.text = "Feed"

                }

                R.id.profile -> {
                    binding.toolbar.text = getString(R.string.profile)
                    goToFragment(profileFrag)
                }
            }
            true
        }
    }

    private fun goToFragment(fragment: Fragment, id: Int = 1) {
        try {
            childFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out
            ).hide(currentFragment).show(fragment).commit()
            currentFragment = fragment

            if (id != 1) {
                lifecycleScope.launch(Dispatchers.Main) {
                    kotlin.runCatching {
                        delay(100)
                        binding.bottomNavigation.selectedItemId = id
                    }
                }
            }
        } catch (_: java.lang.Exception) {
        } catch (_: Exception) {
        }
    }

    private fun launchPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            postNotificationPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }
    }

    private val postNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var once = false
        permissions.entries.forEach {
            val isGranted = it.value
            if (isGranted) {
                if (!once) {
                    myToast(getString(R.string.permission_granted))
                    once = true
                }
            }
        }
    }
}