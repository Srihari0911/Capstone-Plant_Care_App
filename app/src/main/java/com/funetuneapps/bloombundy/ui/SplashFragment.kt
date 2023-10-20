package com.funetuneapps.bloombundy.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var fStore:FirebaseFirestore
    @Inject
    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fStore.collection("users").document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener {
                if (it.exists()){
                    findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
                }else{
                    lifecycleScope.launchWhenResumed {
                        delay(1500)
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }

    }

}