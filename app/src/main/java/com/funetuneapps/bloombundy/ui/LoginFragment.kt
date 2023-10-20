package com.funetuneapps.bloombundy.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.classes.Constants.hide
import com.funetuneapps.bloombundy.classes.Constants.myToast
import com.funetuneapps.bloombundy.classes.Constants.show
import com.funetuneapps.bloombundy.databinding.FragmentLoginBinding
import com.funetuneapps.bloombundy.models.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleClient: GoogleSignInClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginBtn.setOnClickListener {

            launchLogin()
        }


    }

    private fun launchLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id_json)).requestEmail().build()

        googleClient = GoogleSignIn.getClient(requireActivity(), gso)

        //sign in
        val signInIntent = googleClient.signInIntent
        startActivityForResult(signInIntent, 99)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 99) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
                binding.loading.show()
                binding.loginBtn.hide()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                myToast("Some error occurred. Try later.")
            }
        }
    }

    //////logging in with google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val userId = auth.currentUser?.uid
                fStore.collection("users").document(userId.toString()).get().addOnSuccessListener {
                        if (it.exists()) {
                            navigateNext()

                        } else {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                                if (!tokenTask.isSuccessful) {
                                    return@addOnCompleteListener
                                }
                                val token = tokenTask.result
                                val userModel = UserModel(
                                    userName = user!!.displayName.toString(),
                                    userImg = user.photoUrl.toString(),
                                    token = token.toString()
                                )

                                val documentReference: DocumentReference =
                                    fStore.collection("users").document(userId.toString())
                                documentReference.set(userModel).addOnSuccessListener {
                                    binding.loading.hide()
                                    navigateNext()
                                }

                            }
                        }

                    }

            } else {
                myToast("Some error occurred, please try later.")
            }
        }
    }

    private fun navigateNext() {
//        if (SharedPrefUtils.getBool(SharedPrefUtils.firstTime, true)) {
//            SharedPrefUtils.getBool(SharedPrefUtils.firstTime, false)
//            val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
//            findNavController().navigate(
//                R.id.action_loginFragment_to_addPlantFragment, null, navOptions
//            )
//        } else {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
            findNavController().navigate(
                R.id.action_loginFragment_to_mainFragment, null, navOptions
            )
//        }
    }
}