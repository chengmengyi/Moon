package com.demo.newvpn.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.newvpn.BaseAc
import com.demo.newvpn.R
import com.demo.newvpn.moonLogAd
import com.demo.newvpn.show
import com.demo.newvpn.util.AdLimitManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(
    private val type:String,
    private val baseAc: BaseAc,
) {
    private var loop=true
    private var lastNativeAd: NativeAd?=null
    private var showJob: Job?=null
    
    fun showNativeAd(){
        if(AdLimitManager.canRefresh(type)){
            LoadAd.load(type)
            stopNativeAd()
            loop=true
            showJob= GlobalScope.launch(Dispatchers.Main)  {
                delay(300L)
                if (!baseAc.resume){
                    return@launch
                }
                while (loop){
                    if(!isActive){
                        break
                    }
                    val ad = LoadAd.getAdByType(type)
                    if(baseAc.resume&&null!=ad&&ad is NativeAd){
                        cancel()
                        lastNativeAd?.destroy()
                        lastNativeAd=ad
                        loop=false
                        show(ad)
                    }
                    delay(1000L)
                }
            }
        }
    }
    
    private fun show(ad: NativeAd) {
        moonLogAd("show $type ad ")
        val viewNative = baseAc.findViewById<NativeAdView>(R.id.native_view)
        viewNative.iconView=baseAc.findViewById(R.id.native_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(ad.icon?.drawable)

        viewNative.callToActionView=baseAc.findViewById(R.id.native_btn)
        (viewNative.callToActionView as AppCompatTextView).text=ad.callToAction

        viewNative.mediaView=baseAc.findViewById(R.id.native_media)
        ad.mediaContent?.let {
            viewNative.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(8F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }

        viewNative.bodyView=baseAc.findViewById(R.id.native_desc)
        (viewNative.bodyView as AppCompatTextView).text=ad.body


        viewNative.headlineView=baseAc.findViewById(R.id.native_title)
        (viewNative.headlineView as AppCompatTextView).text=ad.headline

        viewNative.setNativeAd(ad)
        baseAc.findViewById<AppCompatImageView>(R.id.iv_cover).show(false)

        AdLimitManager.updateCurrentShow()
        LoadAd.removeAd(type)
        LoadAd.load(type)
        AdLimitManager.setRefreshStatus(type,false)
    }
    
    fun stopNativeAd(){
        loop=false
        showJob?.cancel()
        showJob=null
        AdLimitManager.setRefreshStatus(type,true)
    }
}