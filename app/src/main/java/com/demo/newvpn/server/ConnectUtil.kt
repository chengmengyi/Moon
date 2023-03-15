package com.demo.newvpn.server

import com.demo.newvpn.BaseAc
import com.demo.newvpn.call.IConnectCall
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.conf.FireConf
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectUtil : ShadowsocksConnection.Callback{
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
        state= BaseService.State.Connecting
        GlobalScope.launch {
            if (currentServer.isSuperFast()){
                fastServer=FireConf.getRandomServer()
                DataStore.profileId = fastServer.getServerId()
            }else{
                DataStore.profileId = currentServer.getServerId()
            }
            Core.startService()
        }
    }

    fun disconnect(){
        state= BaseService.State.Stopping
        GlobalScope.launch {
            Core.stopService()
        }
    }

    fun isConnected()= state== BaseService.State.Connected

    fun isDisconnected()= state== BaseService.State.Stopped

    fun connectServerSuccess(connect: Boolean)=if (connect) isConnected() else isDisconnected()

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (isConnected()){
            lastServer= currentServer
            TimeUtil.start()
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
            TimeUtil.start()
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