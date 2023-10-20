package com.funetuneapps.bloombundy.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.classes.Constants
import com.funetuneapps.bloombundy.classes.Constants.show
import com.funetuneapps.bloombundy.classes.LoadingDialog
import com.funetuneapps.bloombundy.databinding.FragmentAddPlantBinding
import com.funetuneapps.bloombundy.databinding.FragmentProfileBinding
import com.funetuneapps.bloombundy.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var fStorage: FirebaseStorage

    private lateinit var binding: FragmentProfileBinding
    private var profilePic: String = ""
    private var imgPath: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProfileData()

        binding.homeBtn.setOnClickListener {
            Constants.mainFragsSwitch(1)
        }
        binding.chatsBtn.setOnClickListener {
            Constants.mainFragsSwitch(2)
        }

        binding.addPlantBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addPlantFragment, null)
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.mainFragment, true).build()
            findNavController().navigate(
                R.id.action_mainFragment_to_loginFragment, null,
                navOptions
            )
        }
        binding.editPicTv.setOnClickListener {
            if (!hasPermission()) {
                getStoragePermission()
            } else {
                launchGallery()
            }
        }

    }

    private fun getProfileData() {
        fStore.collection("users")
            .document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(UserModel::class.java)
                    user?.let {
                        Glide.with(requireContext()).load(user.userImg)
                            .placeholder(ShimmerDrawable().apply {
                                setShimmer(
                                    Shimmer.AlphaHighlightBuilder().setDuration(800)
                                        .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
                                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                                        .setAutoStart(true).build()
                                )
                            }).into(binding.profilePic)
                        binding.personNameTv.text = user.userName
                    }

                }
            }
    }

    private fun uploadImg(callback: () -> Unit) {
            val ref = fStorage.getReference("profile/").child(System.currentTimeMillis().toString())
            ref.putFile(imgPath!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    profilePic = url.toString()
                    fStore.collection("users").document(auth.currentUser?.uid.toString())
                        .update("userImg",profilePic)
                        .addOnSuccessListener {
                            callback()
                        }
                }

            }.addOnFailureListener{

            }

    }

    private fun launchGallery() {
        getContent.launch("image/*")
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imgPath = it
                LoadingDialog.show(requireContext(),"Updating picture..")
                uploadImg {
                    LoadingDialog.hide()
                    getProfileData()
                }
            }


        }

    private fun getStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            handleIntentActivityResult.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            handleIntentActivityResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val handleIntentActivityResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchGallery()
            } else {
                var s = Manifest.permission.READ_EXTERNAL_STORAGE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    s = Manifest.permission.READ_MEDIA_IMAGES
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    s = Manifest.permission.READ_EXTERNAL_STORAGE
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), s)) {
                } else {
                }
            }
        }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result1 = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_MEDIA_IMAGES
            )
            result1 == PackageManager.PERMISSION_GRANTED
        } else {
            val result1 = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            return result1 == PackageManager.PERMISSION_GRANTED
        }
    }
}