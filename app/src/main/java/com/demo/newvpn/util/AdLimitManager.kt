package com.demo.newvpn.util

import com.demo.newvpn.conf.FireConf
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object AdLimitManager {

    private var max_s=50
    private var max_c=15

    private var current_c=0
    private var current_s=0

    private val refresh= hashMapOf<String,Boolean>()

    fun resetRefresh(){
        refresh.clear()
    }

    fun canRefresh(type:String)=refresh[type]?:true

    fun setRefreshStatus(type:String,boolean: Boolean){
        refresh[type]=boolean
    }

    fun setNum(string: String){
        try{
            val jsonObject = JSONObject(string)
            max_c=jsonObject.optInt("max_c")
            max_s=jsonObject.optInt("max_s")
        }catch (e:Exception){

        }
    }

    fun readNum(){
        current_c= MMKV.defaultMMKV().decodeInt(key("current_c"),0)
        current_s= MMKV.defaultMMKV().decodeInt(key("current_s"),0)
    }

    fun updateCurrentClick(){
        current_c++
        MMKV.defaultMMKV().encode(key("current_c"), current_c)
    }

    fun updateCurrentShow(){
        current_s++
        MMKV.defaultMMKV().encode(key("current_s"), current_s)
    }

    fun hasLimit()= current_c>= max_c|| current_s>= max_s

    fun limitInterstitialAd()=FireConf.moonCloak=="1"&&FireConf.cloak

    private fun key(string:String)="${string}...${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}"
}