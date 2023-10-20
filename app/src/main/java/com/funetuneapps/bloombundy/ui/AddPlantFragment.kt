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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.funetuneapps.bloombundy.MainActivity
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.classes.Constants.mToast
import com.funetuneapps.bloombundy.classes.Constants.show
import com.funetuneapps.bloombundy.classes.LoadingDialog
import com.funetuneapps.bloombundy.classes.SharedPrefUtils
import com.funetuneapps.bloombundy.databinding.FragmentAddPlantBinding
import com.funetuneapps.bloombundy.databinding.FragmentLoginBinding
import com.funetuneapps.bloombundy.models.PlantModel
import com.funetuneapps.bloombundy.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AddPlantFragment : Fragment() {
    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var fStorage: FirebaseStorage

    private lateinit var binding: FragmentAddPlantBinding
    private var profilePic: String = ""
    private var imgPath: Uri? = null
    private var imgAdded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlantBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addImageCard.setOnClickListener {
            if (!hasPermission()) {
                getStoragePermission()
            } else {
                launchGallery()
            }
        }
        binding.doneBtn.setOnClickListener {
            updateDoc()
        }
    }

    private fun updateDoc() {
        if (binding.nameEt.text.toString().trim().isEmpty()) {
            mToast("Add plant name first")
            binding.nameEt.error = "Add name here"
        } else if (binding.typeEt.text.toString().trim().isEmpty()
        ) {
            mToast("Add plant type first")
            binding.typeEt.error = "Add name here"
        } else if (
            binding.descEt.text.toString().trim().isEmpty()
        ) {
            mToast("Add plant description first")
            binding.descEt.error = "Add description here"
        } else if (
            binding.sunEt.text.toString().trim().isEmpty() ||
            binding.waterEt.text.toString().trim().isEmpty()
        ) {
            mToast("Add days")
            binding.waterEt.error = "Add days to water here"
            binding.sunEt.error = "Add days to give sunlight here"
        } else {
            if (binding.sunEt.text.toString().toInt() in 2..8
                && binding.waterEt.text.toString().toInt() in 2..8
            ) {
                uploadPlant()
            } else {
                mToast("Add days between 2 to 8")
            }


        }

    }

    private fun uploadPlant() {
        LoadingDialog.show(requireContext(), "Adding plant..")
        val id = UUID.randomUUID().toString()
        uploadImg {
            val plant = PlantModel(
                id = id,
                name = binding.nameEt.text.toString(),
                type = binding.typeEt.text.toString(),
                userId = auth.currentUser?.uid.toString(),
                img = profilePic,
                desc = binding.descEt.text.toString(),
                sunDays = binding.sunEt.text.toString().toInt(),
                waterDays = binding.waterEt.text.toString().toInt(),
                waterTime = System.currentTimeMillis(),
                sunlightTime = System.currentTimeMillis()

            )
            fStore.collection("plants").document(id)
                .set(plant).addOnSuccessListener {
                    fStore.collection("users").document(auth.currentUser?.uid.toString())
                        .get().addOnSuccessListener {
                            val user = it.toObject(UserModel::class.java)
                            val list = ArrayList<String>()
                            user?.let {
                                user.plantsList?.let { plants ->
                                    list.addAll(plants)
                                }
                                list.add(id)
                                fStore.collection("users")
                                    .document(auth.currentUser?.uid.toString())
                                    .update("plantsList", list.toList())
                                (requireActivity() as MainActivity).setAlarmWater(plant)
                                (requireActivity() as MainActivity).setAlarmSunlight(plant)

                                LoadingDialog.hide()
                                navigateNext()
                            }

                        }.addOnSuccessListener {
                            Log.i("dakjfkda", "user failed")
                        }

                }.addOnFailureListener {
                    Log.i("dakjfkda", "updateDoc: plant failed")
                }
        }
    }

    private fun navigateNext() {
        if (SharedPrefUtils.getBool(SharedPrefUtils.firstTime, true)) {
            findNavController().navigate(R.id.action_addPlantFragment_to_mainFragment)
        } else if (findNavController().previousBackStackEntry?.destination?.id == R.id.mainFragment) {
            findNavController().popBackStack()
        } else {
            findNavController().navigate(R.id.action_addPlantFragment_to_mainFragment)
        }
    }


    private fun uploadImg(callback: () -> Unit) {
        if (imgAdded) {
            val ref = fStorage.getReference("profile/").child(System.currentTimeMillis().toString())
            ref.putFile(imgPath!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url ->
                    profilePic = url.toString()
                    Log.i("dakjfkda", "updateDoc: pic success")
                    callback()
                }

            }.addOnFailureListener {
                Log.i("dakjfkda", "updateDoc: pic failed ${it.message}")
            }
        } else {
            callback()
        }
    }

    private fun launchGallery() {
        getContent.launch("image/*")
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imgPath = it
                Glide.with(requireContext()).load(imgPath).into(binding.plantImg)
                binding.plantImg.show()
                imgAdded = true
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