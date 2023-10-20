package com.funetuneapps.bloombundy.models

data class MessageModel(
    val msg: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timeStamp: Long=0L
)
