package com.funetuneapps.bloombundy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.funetuneapps.bloombundy.classes.Constants
import com.funetuneapps.bloombundy.databinding.ItemUserChatBinding
import com.funetuneapps.bloombundy.models.ChatModel
import com.funetuneapps.bloombundy.models.MessageModel
import com.funetuneapps.bloombundy.models.UserModel
import com.funetuneapps.bloombundy.ui.ChatFragment
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val fragment: ChatFragment, val callback: (item: ChatModel) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserChatBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: ChatModel) {
            fragment.fStore.collection("users").document(item.userId).get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(UserModel::class.java)
                    user?.let {
                        binding.userName.text = user.userName
                        if (user.userImg.isNotEmpty()) {
                            Picasso.get().load(user.userImg).placeholder(ShimmerDrawable().apply {
                                setShimmer(
                                    Shimmer.AlphaHighlightBuilder().setDuration(800)
                                        .setBaseAlpha(0.97f).setHighlightAlpha(0.9f)
                                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                                        .setAutoStart(true).build()
                                )
                            }).into(binding.userImg)
                        }
                        getLastMsg(
                            fragment.firebaseAuth.currentUser?.uid.toString(),
                            doc.id,
                            binding.lasMsgTv,
                            binding.msgTime
                        )
                    }
                }
            binding.root.setOnClickListener {
                callback(item)
            }

        }

        private fun getLastMsg(
            senderId: String, receiverId: String, lastMsg: TextView, msgTime: TextView
        ) {
            fragment.fStore.collection("chats")
                .document(fragment.firebaseAuth.currentUser?.uid.toString())
                .collection(senderId + receiverId).orderBy("timeStamp").limit(1).get()
                .addOnSuccessListener {
                    it.documents.forEach { doc ->
                        val msg = doc.toObject(MessageModel::class.java)
                        msg?.let { model ->
                            lastMsg.text = model.msg
                            msgTime.text = Constants.getTimeFromStamp(model.timeStamp)
                        }
                    }
                }
        }


    }

    private val diffCallBack = object : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

}