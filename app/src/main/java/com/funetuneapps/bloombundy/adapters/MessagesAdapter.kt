package com.funetuneapps.bloombundy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.funetuneapps.bloombundy.classes.Constants
import com.funetuneapps.bloombundy.databinding.ItemReceiverChatBinding
import com.funetuneapps.bloombundy.databinding.ItemSenderChatBinding
import com.funetuneapps.bloombundy.models.MessageModel
import com.funetuneapps.bloombundy.ui.MessageFragment
import java.util.*

class MessagesAdapter(
    private val fragment: MessageFragment,
    val callback: (item: MessageModel) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            SenderViewHolder(
                ItemSenderChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceiverViewHolder(
                ItemReceiverChatBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (differ.currentList[position].senderId == fragment.senderId) {
            (holder as SenderViewHolder).bindView(differ.currentList[position])
        } else {
            (holder as ReceiverViewHolder).bindView(differ.currentList[position])
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].senderId == fragment.senderId) {
            0
        } else {
            1
        }
    }

    inner class SenderViewHolder(private val binding: ItemSenderChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: MessageModel) {

            binding.msgTv.text = item.msg
            binding.timeTv.text = Constants.getTimeFromStamp(item.timeStamp)

        }

    }

    inner class ReceiverViewHolder(private val binding: ItemReceiverChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: MessageModel) {
            binding.msgTv.text = item.msg
            binding.timeTv.text = Constants.getTimeFromStamp(item.timeStamp)

        }

    }


    private val diffCallBack = object : DiffUtil.ItemCallback<MessageModel>() {
        override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

}