package com.demo.newvpn.admob

import com.demo.newvpn.bean.AdmobDataBean
import com.demo.newvpn.bean.AdmobResultBean
import com.demo.newvpn.mMoonApp
import com.demo.newvpn.moonLog
import com.demo.newvpn.util.AdLimitManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions

abstract class BaseLoad {

    protected fun loadByType(
        type: String,
        admobDataBean: AdmobDataBean,
        result: (result: AdmobResultBean?) -> Unit
    ) {
        moonLog("start load $type ad, ${admobDataBean.toString()}")
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
                    moonLog("load $type ad success")
                    result.invoke(AdmobResultBean(loadTime = System.currentTimeMillis(), ad = p0))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    moonLog("load $type ad fail,${p0.message}")
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
                    moonLog("load $type ad fail,${p0.message}")
                    result.invoke(null)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    moonLog("load $type ad success")
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
            moonLog("load $type ad success")
            result.invoke(AdmobResultBean(loadTime = System.currentTimeMillis(), ad = p0))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    moonLog("load $type ad fail,${p0.message}")
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
}