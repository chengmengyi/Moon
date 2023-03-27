package com.demo.newvpn.bean

class AdmobResultBean(
    val loadTime:Long=0L,
    val ad:Any?=null
) {
    fun expired()=(System.currentTimeMillis() - loadTime) >=3600L*1000
}