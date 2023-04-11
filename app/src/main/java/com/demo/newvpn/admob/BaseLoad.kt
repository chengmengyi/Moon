package com.demo.newvpn.admob

import com.demo.newvpn.bean.AdmobDataBean
import com.demo.newvpn.bean.AdmobResultBean
import com.demo.newvpn.mMoonApp
import com.demo.newvpn.moonLogAd
import com.demo.newvpn.server.ConnectUtil
import com.demo.newvpn.tba.OkUtil
import com.demo.newvpn.tba.TbaUtil
import com.demo.newvpn.util.AdLimitManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

abstract class BaseLoad {

    private val loadAdIpMap= hashMapOf<String,String>()
    private val loadAdCityMap= hashMapOf<String,String?>()

    protected fun loadByType(
        type: String,
        admobDataBean: AdmobDataBean,
        result: (result: AdmobResultBean?) -> Unit
    ) {
        moonLogAd("start load $type ad, ${admobDataBean.toString()}")
        when (admobDataBean.moontype) {
            "o" -> loadO(type, admobDataBean, result)
            "i" -> loadI(type, admobDataBean, result)
            "n" -> loadN(type, admobDataBean, result)
        }
    }

    private fun loadO(
        type: String,
        admobDataBean: AdmobDataBean,
        result: (result: AdmobResultBean?) -> Unit
    ) {
        AppOpenAd.load(
            mMoonApp,
            admobDataBean.moonadid,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    moonLogAd("load $type ad success")
                    setLoadAdIpCityName(type)
                    p0.setOnPaidEventListener {
                        onAdEvent(type, it, p0.responseInfo, admobDataBean)
                    }
                    result.invoke(AdmobResultBean(loadTime = System.currentTimeMillis(), ad = p0))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    moonLogAd("load $type ad fail,${p0.message}")
                    result.invoke(null)
                }
            }
        )
    }

    private fun loadI(
        type: String,
        admobDataBean: AdmobDataBean,
        result: (result: AdmobResultBean?) -> Unit
    ) {
        InterstitialAd.load(
            mMoonApp,
            admobDataBean.moonadid,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    moonLogAd("load $type ad fail,${p0.message}")
                    result.invoke(null)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    moonLogAd("load $type ad success")
                    setLoadAdIpCityName(type)
                    p0.setOnPaidEventListener {
                        onAdEvent(type, it, p0.responseInfo, admobDataBean)
                    }
                    result.invoke(AdmobResultBean(loadTime = System.currentTimeMillis(), ad = p0))
                }
            }
        )
    }
    private fun loadN(
        type: String,
        admobDataBean: AdmobDataBean,
        result: (result: AdmobResultBean?) -> Unit
    ) {
        AdLoader.Builder(
            mMoonApp,
            admobDataBean.moonadid,
        ).forNativeAd {p0->
            moonLogAd("load $type ad success")
            setLoadAdIpCityName(type)
            p0.setOnPaidEventListener {
                onAdEvent(type, it, p0.responseInfo, admobDataBean)
            }
            result.invoke(AdmobResultBean(loadTime = System.currentTimeMillis(), ad = p0))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    moonLogAd("load $type ad fail,${p0.message}")
                    result.invoke(null)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdLimitManager.updateCurrentClick()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_TOP_LEFT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    private fun onAdEvent(type: String, value: AdValue, responseInfo: ResponseInfo?, adResBean: AdmobDataBean){
        TbaUtil.uploadAdEvent(type,value,responseInfo,adResBean,
            loadAdIpMap[type]?:"",getCurrentIp(),
            loadAdCityMap[type]?:"null",getCurrentCityName())
    }

    private fun setLoadAdIpCityName(adType: String){
        loadAdIpMap[adType]=getCurrentIp()
        loadAdCityMap[adType]=getCurrentCityName()
    }

    private fun getCurrentCityName() = if(ConnectUtil.isConnected()){
        if (ConnectUtil.currentServer.isSuperFast()){
            ConnectUtil.fastServer.city
        }else{
            ConnectUtil.currentServer.city
        }

    }else{
        "null"
    }

    private fun getCurrentIp()=if(ConnectUtil.isConnected()){
        if (ConnectUtil.currentServer.isSuperFast()){
            ConnectUtil.fastServer.ip
        }else{
            ConnectUtil.fastServer.ip
        }
    }else{
        OkUtil.ip
    }
}