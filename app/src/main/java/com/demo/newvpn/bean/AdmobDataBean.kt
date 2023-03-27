package com.demo.newvpn.bean

class AdmobDataBean(
    val moonfrom:String,
    val moonadid:String,
    val moontype:String,
    val moonsort:Int,
) {
    override fun toString(): String {
        return "AdmobDataBean(moonfrom='$moonfrom', moonadid='$moonadid', moontype='$moontype', moonsort=$moonsort)"
    }
}