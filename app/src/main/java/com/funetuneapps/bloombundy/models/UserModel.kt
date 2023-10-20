package com.funetuneapps.bloombundy.models

data class UserModel(
    val userName:String="",
    val userImg:String="",
    val token: String = "",
    val plantsList:List<String>?=null
)
