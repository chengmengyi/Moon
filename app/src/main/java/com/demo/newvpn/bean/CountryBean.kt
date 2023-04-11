package com.demo.newvpn.bean

class CountryBean(
    val countryName:String="Smart Location",
    val cityName:String="",
    val cityId:Int=0,
    val isLocal:Boolean=false
):java.io.Serializable {
    fun fast()=countryName=="Smart Location"
}