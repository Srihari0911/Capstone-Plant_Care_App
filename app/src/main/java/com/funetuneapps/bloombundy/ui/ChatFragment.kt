package com.funetuneapps.bloombundy.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.funetuneapps.bloombundy.adapters.ChatsAdapter
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.classes.BaseFragment
import com.funetuneapps.bloombundy.classes.Constants.hide
import com.funetuneapps.bloombundy.classes.Constants.show
import com.funetuneapps.bloombundy.databinding.FragmentChatBinding
import com.funetuneapps.bloombundy.models.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : BaseFragment() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var fStorage: FirebaseStorage
    lateinit var binding: FragmentChatBinding
    private lateinit var chatAdapter: ChatsAdapter
    private val chatList: ArrayList<ChatModel> by lazy { arrayListOf() }
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (mRootView == null) {
            binding = FragmentChatBinding.inflate(layoutInflater)
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
            setupAdapter()
            getAllChats()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChats() {
        fStore.collection("users").document(firebaseAuth.currentUser?.uid.toString())
            .collection("chatForAds").addSnapshotListener { query, error ->
                query?.let {
                    chatList.clear()
                    query.documents.forEach {
                        val chat = ChatModel(it.id, it.getString("chatAd") ?: "")
                        chatList.add(chat)
                    }
                    if (chatList.isEmpty()) {
                        binding.phView.show()
                        binding.chatsRv.hide()
                    } else {
                        binding.chatsRv.show()
                        binding.phView.hide()
                        chatAdapter.differ.submitList(chatList)
                        chatAdapter.notifyDataSetChanged()
                    }

                }


            }
    }

    private fun setupAdapter() {
        chatAdapter = ChatsAdapter(this) {
            navigateToFragment(R.id.messageFragment, null, Bundle().apply {
                this.putString("userId", it.userId)
                this.putString("adId", it.adId)
            })
        }
        binding.chatsRv.adapter = chatAdapter
    }
}