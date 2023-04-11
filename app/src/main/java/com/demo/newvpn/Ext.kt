package com.demo.newvpn

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.tencent.mmkv.MMKV

fun moonLogAd(string: String){
    if (BuildConfig.DEBUG){
        Log.e("qwerad",string)
    }
}


fun moonLogEvent(string: String){
    if (BuildConfig.DEBUG){
        Log.e("qwerevent",string)
    }
}

fun getLogo(name:String)=when(name.replace(" ".toRegex(), "").toLowerCase()){
    "australia"-> R.drawable.australia
    "belgium"-> R.drawable.belgium
    "brazil"-> R.drawable.brazil
    "canada"-> R.drawable.canada
    "france"-> R.drawable.france
    "hongkong"-> R.drawable.hongkong
    "germany"-> R.drawable.germany
    "india"-> R.drawable.india
    "ireland"-> R.drawable.ireland
    "italy"-> R.drawable.italy
    "koreasouth"-> R.drawable.koreasouth
    "netherlands"-> R.drawable.netherlands
    "newzealand"-> R.drawable.newzealand
    "norway"-> R.drawable.norway
    "singapore"-> R.drawable.singapore
    "sweden"-> R.drawable.sweden
    "switzerland"-> R.drawable.switzerland
    "turkey"-> R.drawable.turkey
    "unitedkingdom"-> R.drawable.unitedkingdom
    "unitedstates"-> R.drawable.unitedstates
    "japan"-> R.drawable.japan
    else-> R.drawable.fast
}


fun processName(applicationContext: Application): String {
    val pid = android.os.Process.myPid()
    var processName = ""
    val manager = applicationContext.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
    for (process in manager.runningAppProcesses) {
        if (process.pid === pid) {
            processName = process.processName
        }
    }
    return processName
}

fun View.show(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

fun Context.toast(string: String){
    Toast.makeText(this,string, Toast.LENGTH_LONG).show()
}

fun Context.getNetStatus(): Int {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}


fun saveNoReferrerTag(){
    MMKV.defaultMMKV().encode("moon_install_no_referrer",1)
}

fun uploadNoReferrerTag()= MMKV.defaultMMKV().decodeInt("moon_install_no_referrer")==1

fun saveHasReferrerTag(){
    MMKV.defaultMMKV().encode("moon_install_has_referrer",1)
}

fun uploadHasReferrerTag()= MMKV.defaultMMKV().decodeInt("moon_install_has_referrer")==1


fun getAdNetWork(string: String):String{
    if(string.contains("facebook")) return "facebook"
    else if(string.contains("admob")) return "admob"
    return ""
}

fun getAdType(adType: String):String{
    when(adType){
        "o"->return "open"
        "i"->return "Interstitial"
        "n"->return "native"
    }
    return ""
}

fun getPrecisionType(precisionType:Int)=when(precisionType){
    1->"ESTIMATED"
    2->"PUBLISHER_PROVIDED"
    3->"PRECISE"
    else->"UNKNOWN"
}

fun str2Int(string: String):Int{
    try {
        return string.toInt()
    }catch (e:Exception){

    }
    return 0
}