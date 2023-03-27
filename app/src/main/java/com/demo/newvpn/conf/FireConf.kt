package com.demo.newvpn.conf

import android.webkit.WebView
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.mMoonApp
import com.demo.newvpn.util.AdLimitManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mmkv.MMKV
import org.json.JSONArray
import org.json.JSONObject

object FireConf {
    var isLimitUser=false

    private val cityList= arrayListOf<String>()
    private val localList= arrayListOf<ServerBean>()
    private val fireList= arrayListOf<ServerBean>()

    fun readFireConf(){
        checkIsLimitUser()
        parseServerJson(LocalConf.localServer, localList)

//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if(it.isSuccessful){
//                parseCity(remoteConfig.getString("moon_getsmart"))
//                parseServerJson(remoteConfig.getString("moon_servlist"), fireList)
//            }
//        }
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
        OkGo.get<String>("https://ipapi.co/json")
            .headers("User-Agent", WebView(mMoonApp).settings.userAgentString)
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    try {
                        isLimitUser = JSONObject(response?.body()?.toString()).optString("country_code").limitArea()
                    }catch (e:Exception){

                    }
                }
            })
    }

    private fun String.limitArea()=contains("IR")||contains("MO")||contains("HK")||contains("CN")

}