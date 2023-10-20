package com.funetuneapps.bloombundy.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.adapters.FeedAdapter
import com.funetuneapps.bloombundy.adapters.HomePlantsAdapter
import com.funetuneapps.bloombundy.classes.Constants.myToast
import com.funetuneapps.bloombundy.databinding.FragmentFeedBinding
import com.funetuneapps.bloombundy.databinding.FragmentHomeBinding
import com.funetuneapps.bloombundy.models.PlantModel
import com.funetuneapps.bloombundy.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false
    private lateinit var feedAdapter: FeedAdapter
    private lateinit var binding: FragmentFeedBinding
    private val plantsList=ArrayList<PlantModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentFeedBinding.inflate(layoutInflater)
            mRootView = binding.root
            mIsFirstLoad = true
        } else {
            mIsFirstLoad = false
        }
        return mRootView as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mIsFirstLoad){

            setUpAdapter()

        }
    }

    private fun setUpAdapter() {
        feedAdapter= FeedAdapter(this){
            if (it != auth.currentUser?.uid.toString()) {
                findNavController().navigate(R.id.action_mainFragment_to_messageFragment,
                    Bundle().apply
                 {
                     this.putString("userId", it)
                 })

            } else {
                myToast("Cannot connect with yourself")
            }
        }
        binding.plantsRv.adapter=feedAdapter

        fStore.collection("plants").get().addOnSuccessListener {
            for (doc in it.documents){
                val plant=doc.toObject(PlantModel::class.java)
                plant?.let {
                    if (plant.userId!=auth.currentUser?.uid.toString()){
                        plantsList.add(plant)
                    }
                }
            }
            feedAdapter.differ.submitList(plantsList)
            feedAdapter.notifyDataSetChanged()
        }
    }
}