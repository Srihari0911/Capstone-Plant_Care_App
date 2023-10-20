package com.funetuneapps.bloombundy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.funetuneapps.bloombundy.classes.Constants
import com.funetuneapps.bloombundy.databinding.ItemHomePlantsBinding
import com.funetuneapps.bloombundy.models.PlantModel
import com.funetuneapps.bloombundy.ui.HomeFragment
import com.google.firebase.firestore.ktx.toObject

class HomePlantsAdapter(
    private val fragment: HomeFragment, val callback: (item: String) -> Unit
) : RecyclerView.Adapter<HomePlantsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomePlantsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val binding: ItemHomePlantsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: String) {

            fragment.fStore.collection("plants").document(item)
                .get().addOnSuccessListener {
                    val plant=it.toObject(PlantModel::class.java)
                    plant?.let {plant->
                        binding.plantName.text = plant.name
                        binding.plantType.text = plant.type
                        binding.plantDesc.text = plant.desc

                        binding.water.text = Constants.waterTimeDifference(plant.waterDays,plant.waterTime).toString()+"h"
                        binding.sunlight.text = Constants.sunlightTimeDifference(plant.sunDays,plant.sunlightTime).toString()+"h"
                        Glide.with(fragment.requireContext()).load(plant.img)
                            .placeholder(ShimmerDrawable().apply {
                                setShimmer(
                                    Shimmer.AlphaHighlightBuilder().setDuration(800)
                                        .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
                                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                                        .setAutoStart(true).build()
                                )
                            }).into(binding.plantImg)
                    }
                }

        }

    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

}