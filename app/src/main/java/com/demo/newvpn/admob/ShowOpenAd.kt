package com.demo.newvpn.admob

import com.demo.newvpn.BaseAc
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.moonLog
import com.demo.newvpn.util.AdLimitManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowOpenAd(
    private val type:String,
    private val baseAc: BaseAc
) {

    private var close: (() -> Unit?)? =null

    fun showOpenAd(back:Boolean=false,showing:()->Unit,close:()->Unit){
        this.close=close
        val ad = LoadAd.getAdByType(type)
        if (null!=ad){
            if (LoadAd.openAdShowing||!baseAc.resume){
                close.invoke()
                return
            }
            moonLog("show $type ad")
            showing.invoke()
            when(ad){
                is InterstitialAd ->{
                    ad.fullScreenContentCallback= openAdCallback
                    ad.show(baseAc)
                }
                is AppOpenAd ->{
                    ad.fullScreenContentCallback= openAdCallback
                    ad.show(baseAc)
                }
            }
        }else{
            if (back){
                LoadAd.load(type)
                close.invoke()
            }
        }
    }

    private val openAdCallback=object : FullScreenContentCallback(){

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            LoadAd.openAdShowing =false
            adClosed()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            LoadAd.openAdShowing  =true
            AdLimitManager.updateCurrentShow()
            LoadAd.removeAd(type)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            LoadAd.openAdShowing  =false
            LoadAd.removeAd(type)
            adClosed()
        }


        override fun onAdClicked() {
            super.onAdClicked()
            AdLimitManager.updateCurrentClick()
        }
    }

    private fun adClosed(){
        if (type!= LocalConf.OPEN){
            LoadAd.load(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (baseAc.resume){
                close?.invoke()
            }
        }
    }
}