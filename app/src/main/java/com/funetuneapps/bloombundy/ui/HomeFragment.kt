package com.funetuneapps.bloombundy.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.adapters.HomePlantsAdapter
import com.funetuneapps.bloombundy.classes.Constants.show
import com.funetuneapps.bloombundy.databinding.FragmentHomeBinding
import com.funetuneapps.bloombundy.databinding.FragmentMainBinding
import com.funetuneapps.bloombundy.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false
    private lateinit var plantsAdapter: HomePlantsAdapter
    private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentHomeBinding.inflate(layoutInflater)
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
            setUpAdapter()
        }
    }

    private fun setUpAdapter() {
        plantsAdapter = HomePlantsAdapter(this) {}
        binding.plantsRv.adapter = plantsAdapter

        fStore.collection("users").document(auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener {
                val user = it.toObject(UserModel::class.java)
                user?.let {
                    if (it.plantsList.isNullOrEmpty()) {
                        binding.phView.show()
                    } else {
                        plantsAdapter.differ.submitList(it.plantsList)
                    }
                }
            }
    }
}