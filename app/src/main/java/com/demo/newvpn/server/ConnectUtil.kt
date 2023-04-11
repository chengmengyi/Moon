package com.demo.newvpn.server

import android.os.Bundle
import com.demo.newvpn.BaseAc
import com.demo.newvpn.admob.LoadAd
import com.demo.newvpn.call.IConnectCall
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.tba.OkUtil
import com.demo.newvpn.util.PointSet
import com.demo.newvpn.util.ReferrerUtil
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ConnectUtil : ShadowsocksConnection.Callback{
    private var connecting=false
    private var baseAc:BaseAc?=null
    var state = BaseService.State.Stopped
    var currentServer= ServerBean()
    var lastServer= ServerBean()
    var fastServer= ServerBean()
    private val sc= ShadowsocksConnection(true)
    private var iConnectCall: IConnectCall?=null

    fun init(baseAc:BaseAc,iConnectCall: IConnectCall){
        this.baseAc=baseAc
        this.iConnectCall=iConnectCall
        sc.connect(baseAc,this)
    }

    fun connect(){
        if(currentServer.isLocal){
            if(currentServer.isSuperFast()){
                connectServer(LocalConf.localServerList.random())
            }else{
                connectServer(currentServer)
            }
        }else{
            if(currentServer.cityId==0){
                OkUtil.getServerInfoByCityId(null,0){
                    fastServer=it
                    connectServer(it)
                }
            }else{
                connectServer(currentServer)
            }
        }
    }

    private fun connectServer(serverBean: ServerBean){
        state= BaseService.State.Connecting
        GlobalScope.launch {
            delay(500L)
            DataStore.profileId = serverBean.getServerId()
            val bundle = Bundle()
            bundle.putBoolean("Isbuy",ReferrerUtil.isBuyUser())
            PointSet.point("moon_startvpn",bundle=bundle)
            Core.startService()
        }
    }

    fun disconnect(){
        state= BaseService.State.Stopping
        GlobalScope.launch {
            val bundle = Bundle()
            bundle.putInt("time",TimeUtil.getTimeInt())
            PointSet.point("moon_stratime",bundle=bundle)
            Core.stopService()
        }
    }

    fun isConnected()= state== BaseService.State.Connected

    fun isDisconnected()= state== BaseService.State.Stopped

    fun isConnectingOrStopping()= state== BaseService.State.Connecting||state== BaseService.State.Stopping

    fun connectServerSuccess(connect: Boolean)=if (connect) isConnected() else isDisconnected()

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (isConnected()){
            lastServer= currentServer
            PointSet.point("moon_vpnt")
            if(FireConf.isPlanB){
                LoadAd.removeAllAd()
            }
        }
        if (isDisconnected()){
            TimeUtil.end()
            iConnectCall?.disconnectSuccess()
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.state=state
        if (isConnected()){
            lastServer= currentServer
            iConnectCall?.connectSuccess()
        }
    }

    override fun onBinderDied() {
        baseAc?.let {
            sc.disconnect(it)
        }
    }

    fun onDestroy(){
        onBinderDied()
        baseAc=null
        iConnectCall=null
    }
}