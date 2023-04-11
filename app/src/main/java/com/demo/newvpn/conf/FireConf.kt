package com.demo.newvpn.conf

import android.webkit.WebView
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.mMoonApp
import com.demo.newvpn.str2Int
import com.demo.newvpn.tba.OkUtil
import com.demo.newvpn.tba.OkUtil.checkUserCloak
import com.demo.newvpn.util.AdLimitManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mmkv.MMKV
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

object FireConf {
    var isLimitUser=false
    var cloak=true
    private var planb_start="1"
    private var planb_ratio="100"
    var isPlanB=false
    var moonCloak="2"

    private val cityList= arrayListOf<String>()
    private val localList= arrayListOf<ServerBean>()
    private val fireList= arrayListOf<ServerBean>()

    fun readFireConf(){
        checkIsLimitUser()
        parseServerJson(LocalConf.localServer, localList)

//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if(it.isSuccessful){
//                parsePlanConfig(remoteConfig.getString("moon_config"))
//
//                val string = remoteConfig.getString("moon_cloak")
//                if (string.isNotEmpty()){
//                    moonCloak=string
//                }
//            }
//        }
    }

    private fun parsePlanConfig(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            planb_start=jsonObject.optString("moon_start")
            planb_ratio=jsonObject.optString("moon_ratio")
        }
    }

    private fun saveAd(string: String){
        AdLimitManager.setNum(string)
        MMKV.defaultMMKV().encode("moon_ad",string)
    }

    fun getAdString():String{
        val value = MMKV.defaultMMKV().decodeString("moon_ad") ?: ""
        if(value.isEmpty()){
            return LocalConf.localAd
        }
        return value
    }

    private fun parseCity(string: String){
        runCatching {
            cityList.clear()
            val jsonArray = JSONArray(string)
            for (index in 0 until jsonArray.length()){
                cityList.add(jsonArray.optString(index))
            }
        }
    }
    
    private fun parseServerJson(json:String,list:ArrayList<ServerBean>){
        runCatching {
            val jsonArray = JSONArray(json)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    ServerBean(
                        pwd = jsonObject.optString("moon_getpwd"),
                        account = jsonObject.optString("moon_getaccount"),
                        port = jsonObject.optInt("moon_getport"),
                        country =jsonObject.optString("moon_getcountry"),
                        city =jsonObject.optString("moon_getcity"),
                        ip=jsonObject.optString("moon_getip")
                    )
                )
            }
            list.forEach { it.writeServerId() }
        }
    }

    fun getAllServerList()= fireList.ifEmpty { localList }

    fun getRandomServer():ServerBean{
        val serverList = getAllServerList()
        if (!cityList.isNullOrEmpty()){
            val filter = serverList.filter { cityList.contains(it.city) }
            if (!filter.isNullOrEmpty()){
                return filter.random()
            }
        }
        return serverList.random()
    }

    private fun checkIsLimitUser(){
        OkUtil.requestIp {
            checkUserCloak()
//            isLimitUser = if(OkUtil.countryCode.isNotEmpty()){
//                OkUtil.countryCode.limitArea()
//            }else{
//                Locale.getDefault().country.limitArea()
//            }
        }
    }

    fun checkIsPlanB(isColdLoad: Boolean){
        if((isColdLoad&&planb_start=="1")||planb_start=="2"){
            val nextInt = Random().nextInt(100)
            isPlanB = str2Int(planb_ratio)>=nextInt
        }
    }

    private fun String.limitArea()=contains("IR")||contains("MO")||contains("HK")||contains("CN")

}