package com.funetuneapps.bloombundy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.databinding.ItemFeedBinding
import com.funetuneapps.bloombundy.databinding.ItemHomePlantsBinding
import com.funetuneapps.bloombundy.models.PlantModel
import com.funetuneapps.bloombundy.models.UserModel
import com.funetuneapps.bloombundy.ui.FeedFragment
import com.funetuneapps.bloombundy.ui.HomeFragment
import com.squareup.picasso.Picasso

class FeedAdapter(
    private val fragment: FeedFragment, val callback: (item: String) -> Unit
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFeedBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: PlantModel) {
            binding.plantName.text = item.name
            binding.plantType.text = item.type
            binding.plantDesc.text = item.desc
            Glide.with(fragment.requireContext()).load(item.img)
                .placeholder(ShimmerDrawable().apply {
                    setShimmer(
                        Shimmer.AlphaHighlightBuilder().setDuration(800)
                            .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
                            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                            .setAutoStart(true).build()
                    )
                }).into(binding.plantImg)
            fragment.fStore.collection("users").document(item.userId).get().addOnSuccessListener {
                val user = it.toObject(UserModel::class.java)
                user?.let {
                    Picasso.get().load(it.userImg).error(R.drawable.place_holder_user)
                        .placeholder(ShimmerDrawable().apply {
                            setShimmer(
                                Shimmer.AlphaHighlightBuilder().setDuration(800)
                                    .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
                                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                                    .setAutoStart(true).build()
                            )
                        })
                        .into(binding.userImg)
                    binding.userName.text = user.userName
                }
            }

            binding.connectBtn.setOnClickListener {
                callback(item.userId)
            }
        }

    }

    private val diffCallBack = object : DiffUtil.ItemCallback<PlantModel>() {
        override fun areItemsTheSame(oldItem: PlantModel, newItem: PlantModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PlantModel, newItem: PlantModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

}