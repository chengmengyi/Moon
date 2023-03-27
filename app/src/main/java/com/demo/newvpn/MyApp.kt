package com.demo.newvpn

import android.app.Application
import com.demo.newvpn.ac.HomeAc
import com.demo.newvpn.conf.FireConf
import com.github.shadowsocks.Core
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Core.init(this,HomeAc::class)
        if (!packageName.equals(processName(this))){
            return
        }
        Firebase.initialize(this)
        MMKV.initialize(this)
        FireConf.readFireConf()
        RegisterAc.register(this)
    }
}