package com.demo.newvpn.conf

import com.demo.newvpn.bean.ServerBean
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
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
    }
    
    private fun parseServerJson(json:String,list:ArrayList<ServerBean>){
        runCatching {
            val jsonArray = JSONArray(json)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                localList.add(
                    ServerBean(
                        pwd = jsonObject.optString("ma"),
                        account = jsonObject.optString("hao"),
                        port = jsonObject.optInt("kou"),
                        country =jsonObject.optString("ji"),
                        city =jsonObject.optString("ty"),
                        ip=jsonObject.optString("ip")
                    )
                )
            }
            localList.forEach { it.writeServerId() }
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
        OkGo.get<String>("https://api.myip.com/")
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
//                        ipJson="""{"ip":"89.187.185.11","country":"United States","cc":"IR"}"""
                    try {
                        isLimitUser = JSONObject(response?.body()?.toString()).optString("cc").limitArea()
                    }catch (e:Exception){

                    }
                }
            })
    }

    private fun String.limitArea()=contains("IR")||contains("MO")||contains("HK")||contains("CN")

}