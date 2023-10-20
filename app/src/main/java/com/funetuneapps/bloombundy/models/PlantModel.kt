package com.funetuneapps.bloombundy.models

data class PlantModel(
    val id:String="",
    val name:String="",
    val type:String="",
    val desc:String="",
    val img:String="",
    val userId:String="",
    val waterDays:Int=0,
    val sunDays:Int=0,
    val waterTime:Long=0L,
    val sunlightTime:Long=0L
)
