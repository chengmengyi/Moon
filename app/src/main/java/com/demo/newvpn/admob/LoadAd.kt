package com.demo.newvpn.admob

import com.demo.newvpn.bean.AdmobDataBean
import com.demo.newvpn.bean.AdmobResultBean
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.moonLogAd
import com.demo.newvpn.util.AdLimitManager
import org.json.JSONObject

object LoadAd:BaseLoad() {
    var openAdShowing=false
    private val loadingList= arrayListOf<String>()
    private val adResultMap = hashMapOf<String,AdmobResultBean>()

    fun load(type:String,tryNum:Int=0){
        if(AdLimitManager.hasLimit()){
            moonLogAd("limit")
            return
        }
        if((type==LocalConf.CONNECT||type==LocalConf.BACK)&&AdLimitManager.limitInterstitialAd()){
            moonLogAd("cloak user limit")
            return
        }
        if(loadingList.contains(type)){
            moonLogAd("$type loading")
            return
        }
        if(adResultMap.containsKey(type)){
            val resultAdBean = adResultMap[type]
            if(null!=resultAdBean?.ad){
                if(resultAdBean.expired()){
                    removeAd(type)
                }else{
                    moonLogAd("$type cache")
                    return
                }
            }
        }
        val parseAdList = getAdListByType(type)
        if(parseAdList.isEmpty()){
            return
        }
        loadingList.add(type)
        loopLoadAd(type,parseAdList.iterator(),tryNum)
    }

    private fun loopLoadAd(type: String, iterator: Iterator<AdmobDataBean>, tryNum:Int){
        loadByType(type,iterator.next()){
            if(null!=it){
                loadingList.remove(type)
                adResultMap[type]=it
            }else{
                if(iterator.hasNext()){
                    loopLoadAd(type,iterator,tryNum)
                }else{
                    loadingList.remove(type)
                    if(tryNum>0&&type==LocalConf.OPEN){
                        load(type, tryNum=0)
                    }
                }
            }
        }
    }


    private fun getAdListByType(key:String):List<AdmobDataBean>{
        val list= arrayListOf<AdmobDataBean>()
        try {
            val jsonArray = JSONObject(FireConf.getAdString()).getJSONArray(key)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    AdmobDataBean(
                        jsonObject.optString("moonfrom"),
                        jsonObject.optString("moonadid"),
                        jsonObject.optString("moontype"),
                        jsonObject.optInt("moonsort"),
                    )
                )
            }
        }catch (e:Exception){
        }
        return list.filter { it.moonfrom == "admob" }.sortedByDescending { it.moonsort }
    }

    fun preAllAd(){
        load(LocalConf.OPEN)
        load(LocalConf.HOME_BOTTOM)
        load(LocalConf.RESULT_BOTTOM)
        load(LocalConf.CONNECT)
    }

    fun getAdByType(type: String)= adResultMap[type]?.ad

    fun removeAd(type: String){
        adResultMap.remove(type)
    }

    fun removeAllAd(){
        adResultMap.clear()
        loadingList.clear()
        preAllAd()
        load(LocalConf.BACK)
    }

    fun checkDisconnectAd(){
        val list= arrayListOf(LocalConf.CONNECT,LocalConf.BACK,LocalConf.HOME_BOTTOM,LocalConf.RESULT_BOTTOM,)
        for (type in list) {
            val resultBean = adResultMap[type]
            if (null==resultBean||resultBean.expired()){
                load(type)
            }
        }
    }
}