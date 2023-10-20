package com.funetuneapps.bloombundy.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.funetuneapps.bloombundy.R
import com.funetuneapps.bloombundy.adapters.MessagesAdapter
import com.funetuneapps.bloombundy.classes.BaseFragment
import com.funetuneapps.bloombundy.classes.Constants.sendNotification
import com.funetuneapps.bloombundy.databinding.FragmentMessageBinding
import com.funetuneapps.bloombundy.models.MessageModel
import com.funetuneapps.bloombundy.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MessageFragment : BaseFragment() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var fStore: FirebaseFirestore

    @Inject
    lateinit var fStorage: FirebaseStorage
    lateinit var binding: FragmentMessageBinding
    var senderId = ""
    private var receiverId = ""
    private var senderRoom = ""
    private var receiverRoom = ""
    private var adId: String = ""
    private lateinit var messageAdapter: MessagesAdapter
    private val msgList: ArrayList<MessageModel> by lazy { arrayListOf() }
    private lateinit var listener: ListenerRegistration
    private var receiverToken: String = ""
    private var senderName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(layoutInflater)
        setStatusBarColorStart()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        FirebaseMessaging.getInstance().subscribeToTopic("all")

        setupAdapter()
        arguments?.getString("userId")?.let { id ->
            fStore.collection("users").document(id).get().addOnSuccessListener { doc ->
                val user = doc.toObject(UserModel::class.java)
                user?.let {
                    Picasso.get().load(it.userImg).placeholder(R.color.gray).into(binding.userPic)
                    binding.userNameTv.text = it.userName
                    receiverToken = it.token
                }
            }
            receiverId = id
            firebaseAuth.currentUser?.let {
                senderId = it.uid
                senderRoom = senderId + receiverId
                receiverRoom = receiverId + senderId
                getAllMessages()
                fStore.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                    val user = doc.toObject(UserModel::class.java)
                    user?.let {
                        senderName = user.userName
                    }
                }
            }
        }
        arguments?.getString("adId")?.let {
            adId = it
        }

        binding.ivBack.setOnClickListener {
            setStatusBarColorEnd()
            findNavController().popBackStack()
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    setStatusBarColorEnd()
                    findNavController().popBackStack()
                }

            })

        binding.sendBtn.setOnClickListener {
            if (binding.messageEt.text.trim().isNotEmpty()) {
                val msg = MessageModel(
                    binding.messageEt.text.toString(),
                    senderId,
                    receiverId,
                    System.currentTimeMillis()
                )
                msgList.add(msg)
                messageAdapter.differ.submitList(msgList)
                messageAdapter.notifyItemInserted(msgList.size - 1)
                requireActivity().sendNotification(
                    receiverToken,
                    senderName,
                    binding.messageEt.text.toString()
                )
                binding.messageEt.text.clear()
                binding.messagesRv.smoothScrollToPosition(msgList.size - 1)
                uploadMessageDatabase(msg)

            }

        }


    }


    private fun setupAdapter() {
        messageAdapter = MessagesAdapter(this) {}
        binding.messagesRv.adapter = messageAdapter
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun uploadMessageDatabase(msg: MessageModel) {
        val ad = hashMapOf("chatAd" to adId)

        fStore.collection("users").document(receiverId).collection("chatForAds").document(senderId)
            .set(ad).addOnSuccessListener {
                fStore.collection("users").document(senderId).collection("chatForAds")
                    .document(receiverId)
                    .set(ad).addOnSuccessListener {
                        fStore.collection("chats").document(senderId).collection(senderRoom)
                            .document()
                            .set(msg).addOnSuccessListener {

                                fStore.collection("chats").document(receiverId)
                                    .collection(receiverRoom)
                                    .document().set(msg)
                            }
                    }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllMessages() {
        listener = fStore.collection("chats").document(senderId).collection(senderRoom)
            .orderBy("timeStamp").addSnapshotListener { query, _ ->
                query?.let {
                    msgList.clear()
                    query.documents.forEach { doc ->
                        val msg = doc.toObject(MessageModel::class.java)
                        msg?.let {
                            msgList.add(it)
                        }
                    }
                    messageAdapter.differ.submitList(msgList)
                    messageAdapter.notifyDataSetChanged()
                    if (msgList.isNotEmpty()) {
                        binding.messagesRv.smoothScrollToPosition(msgList.size - 1)
                    }
                }

            }
    }

    override fun onDestroyView() {
        listener.remove()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onDestroyView()
    }

    private fun setStatusBarColorStart() {
        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.containerColor, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }

    private fun setStatusBarColorEnd() {

        activity?.apply {
            val window: Window = this.window
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
            window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.white, null)
        }
    }

}