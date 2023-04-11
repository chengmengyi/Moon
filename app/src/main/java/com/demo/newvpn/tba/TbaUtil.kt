package com.demo.newvpn.tba

import android.webkit.WebSettings
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.demo.newvpn.*
import com.demo.newvpn.bean.AdmobDataBean
import com.github.shadowsocks.Core
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.ResponseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

object TbaUtil {

    fun uploadEvent(){
        OkUtil.requestIp {
            uploadInstallEvent()
            uploadSessionEvent()
        }
    }

    private fun uploadSessionEvent(){
        GlobalScope.launch {
            val createTbaCommonJson = createTbaCommonJson()
            createTbaCommonJson.put("piggy","coop")
            OkUtil.uploadEvent(createTbaCommonJson)
        }
    }

    private fun uploadInstallEvent(){
        if (!uploadHasReferrerTag() || !uploadNoReferrerTag()){
            val referrerClient = InstallReferrerClient.newBuilder(Core.app).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val response = referrerClient.installReferrer
                                createInstallJson(response)
                            }
                            else->{
                                createInstallJson(null)
                            }
                        }
                    }
                    runCatching {
                        referrerClient.endConnection()
                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }
    }

    private fun createInstallJson(response: ReferrerDetails?) {
        if (null==response&& uploadNoReferrerTag()){
            return
        }
        if (null!=response&& uploadHasReferrerTag()){
            return
        }
        GlobalScope.launch {
            val createTbaCommonJson = createTbaCommonJson()
            createTbaCommonJson.put("gritty",JSONObject().apply {
                put("deject",TbaCommon.getBuild())
                put("harm", WebSettings.getDefaultUserAgent(mMoonApp))
                put("clam","khmer")
                put("chicano",TbaCommon.getFirstInstallTime(mMoonApp))
                put("cairn",TbaCommon.getLastUpdateTime(mMoonApp))
                if (null==response){
                    put("aghast","")
                    put("varsity","")
                    put("factual", 0)
                    put("thought", 0)
                    put("rumple", 0)
                    put("idle", 0)
                    put("blob", false)
                }else{
                    put("aghast",response.installReferrer)
                    put("varsity",response.installVersion)
                    put("factual", response.referrerClickTimestampSeconds)
                    put("thought", response.installBeginTimestampSeconds)
                    put("rumple", response.referrerClickTimestampServerSeconds)
                    put("idle", response.installBeginTimestampServerSeconds)
                    put("blob", response.googlePlayInstantParam)
                }
            })
            OkUtil.uploadEvent(createTbaCommonJson,install = true)
        }
    }


    fun uploadAdEvent(
        type: String,
        value: AdValue,
        responseInfo: ResponseInfo?,
        adResBean: AdmobDataBean,
        loadIp:String,
        showIp:String,
        loadCity:String,
        showCity:String
    ){
        GlobalScope.launch {
            val createTbaCommonJson = createTbaCommonJson()
            withContext(Dispatchers.Main){
                val adJson = JSONObject()
                adJson.apply {
                    put("hodge",value.valueMicros)
                    put("filth",value.currencyCode)
                    put("moan",getAdNetWork(responseInfo?.mediationAdapterClassName?:""))
                    put("hewett","admob")
                    put("fiery",adResBean.moonadid)
                    put("dreamt",type)
                    put("escort","")
                    put("domino",getAdType(adResBean.moontype))
                    put("happy",getPrecisionType(value.precisionType))
                    put("gemma",loadIp)
                    put("alphabet",showIp)
                    put("vanish","fiat")
                }
                createTbaCommonJson.put("hell",adJson)
                createTbaCommonJson.put("linton#req_city",loadCity)
                createTbaCommonJson.put("linton#imp_city",showCity)
                OkUtil.uploadEvent(createTbaCommonJson)
            }

        }
    }

    private fun createTbaCommonJson()= JSONObject().apply {
        put("feeble",TbaCommon.getBrand())
        put("border", TbaCommon.getAppVersionCode(mMoonApp))
        put("library", TbaCommon.getAndroidId(mMoonApp))
        put("burnout", false)
        put("context", TbaCommon.getLogId())
        put("bilayer", TbaCommon.getDistinctId(mMoonApp))
        put("foist", TbaCommon.getGaid(mMoonApp))
        put("kick", TbaCommon.getScreenRes(mMoonApp))
        put("lumbago", TbaCommon.getOsVersion())
        put("lotion", TbaCommon.getZoneOffset())
        put("geiger", System.currentTimeMillis())
        put("demigod", TbaCommon.getSystemLanguage())
        put("gs", "casual")
        put("standeth", TbaCommon.getBundleId(mMoonApp))
        put("manley", TbaCommon.getOperator(mMoonApp))
        put("linear", TbaCommon.getDeviceModel())
        put("fright", TbaCommon.getOsCountry())
        put("rutabaga", TbaCommon.getManufacturer())
        put("semi", TbaCommon.getNetworkType(mMoonApp))
        put("reprisal", TbaCommon.getAppVersion(mMoonApp))
        put("curtsey", OkUtil.ip)
    }
}