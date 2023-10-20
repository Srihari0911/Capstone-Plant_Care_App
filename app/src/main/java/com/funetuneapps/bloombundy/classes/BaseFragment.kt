package com.funetuneapps.bloombundy.classes
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseFragment : Fragment() {

    private fun NavController.isFragmentRemovedFromBackStack(destinationId: Int) =
        try {
            getBackStackEntry(destinationId)
            false
        } catch (e: Exception) {
            true
        }

    private val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.nav_frags_zoom_out)
        .setExitAnim(R.anim.come_out_screen)
        .setPopEnterAnim(R.anim.go_in_screen)
        .setPopExitAnim(R.anim.nav_frags_zoom_in).build()

    fun navigateToFragment(fragmentId: Int, actionId: Int?, bundle: Bundle = bundleOf()) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                findNavController().apply {
                    if (currentDestination?.id != fragmentId) {
                        if (!isFragmentRemovedFromBackStack(fragmentId)) {
                            popBackStack(fragmentId, false)
                        } else {
                            try {
                                if (actionId != null)
                                    navigate(actionId, bundle, navOptions)
                                else
                                    navigate(fragmentId, bundle, navOptions)
                            } catch (e: Exception) {
                                navigate(fragmentId, bundle, navOptions)
                            }
                        }
                    }
                }
            } catch (_: java.lang.Exception) {
            } catch (_: Exception) {
            }
        }
    }

}