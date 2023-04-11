package com.demo.newvpn.util

import android.os.Bundle
import com.demo.newvpn.BuildConfig
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.moonLogEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object PointSet {
    private var remoteConfig: FirebaseAnalytics?=null
    init {
        if (!BuildConfig.DEBUG){
            remoteConfig=Firebase.analytics
        }
    }

    fun point(key:String,bundle:Bundle= Bundle()){
        moonLogEvent("point==$key==")
        remoteConfig?.logEvent(key,bundle)
    }

    fun setUserProperty(user:String=if (FireConf.isPlanB) "B" else "A"){
        moonLogEvent("point==setUserProperty==${user}")
        remoteConfig?.setUserProperty("moon_user",user)
    }
}