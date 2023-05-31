package com.demo.newvpn

import android.app.Application
import com.demo.newvpn.ac.HomeAc
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.tba.TbaUtil
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

lateinit var mMoonApp: MyApp
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        mMoonApp=this
        Core.init(this,HomeAc::class)
        if (!packageName.equals(processName(this))){
            return
        }
        Firebase.initialize(this)
        MobileAds.initialize(this)
        MMKV.initialize(this)
        FireConf.readFireConf()
        RegisterAc.register(this)
        TbaUtil.uploadEvent()
    }
}