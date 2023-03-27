package com.demo.newvpn

import android.app.Application
import com.demo.newvpn.ac.HomeAc
import com.demo.newvpn.conf.FireConf
import com.github.shadowsocks.Core
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
        MMKV.initialize(this)
        FireConf.readFireConf()
        RegisterAc.register(this)
    }
}