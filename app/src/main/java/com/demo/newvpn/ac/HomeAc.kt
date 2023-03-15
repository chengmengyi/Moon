package com.demo.newvpn.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.SizeUtils
import com.demo.newvpn.*
import com.demo.newvpn.call.IConnectCall
import com.demo.newvpn.call.ITimeCall
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.server.ConnectUtil
import com.demo.newvpn.server.TimeUtil
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.drawer_home.*
import java.lang.Exception

class HomeAc :BaseAc(), ITimeCall, IConnectCall {
    private var canClick=true
    private var permission=false
    private val maxPx= SizeUtils.dp2px(104F)
    private var translationX: ValueAnimator?=null

    private val registerResult=registerForActivityResult(StartService()) {
        if (!it && permission) {
            permission = false
            startConnectServer()
        } else {
            canClick=true
            toast("Connected fail")
        }
    }

    override fun layout(): Int = R.layout.activity_home

    override fun initView() {
        immersionBar.statusBarView(top).init()
        TimeUtil.setInterface(this)
        ConnectUtil.init(this,this)
        setClick()
    }

    private fun setClick(){
        view_connect.setOnClickListener { clickConnectView() }
        llc_server_list.setOnClickListener {
            if(canClick&&!drawer_layout.isOpen&&!guideIsShowing()){
                startActivityForResult(Intent(this,ServerListAc::class.java),313)
            }
        }
        iv_set.setOnClickListener {
            if(canClick&&!drawer_layout.isOpen&&!guideIsShowing()){
                drawer_layout.openDrawer(Gravity.LEFT)
            }
        }
        llc_contact.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                try {
                    val uri = Uri.parse("mailto:${LocalConf.email}")
                    val intent = Intent(Intent.ACTION_SENDTO, uri)
                    startActivity(intent)
                }catch (e: Exception){
                    toast("Contact us by email：${LocalConf.email}")
                }
            }
        }
        llc_update.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                val packName = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
                }
                startActivity(intent)
            }
        }
        llc_share.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                val pm = packageManager
                val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${packageName}"
                )
                startActivity(Intent.createChooser(intent, "share"))
            }
        }
        llc_privacy.setOnClickListener {
            if(canClick&&drawer_layout.isOpen){
                startActivity(Intent(this,WebAc::class.java))
            }
        }
    }

    private fun clickConnectView(){
        if(FireConf.isLimitUser){
            AlertDialog.Builder(this).apply {
                setCancelable(false)
                setMessage("Due to the policy reason , this service is not available in your country")
                setPositiveButton("confirm") { _, _ ->
                    finish()
                }
                show()
            }
        }
        if(!canClick){
            return
        }
        canClick=false
        if(ConnectUtil.isConnected()){
            updateConnectingView()
            ConnectUtil.disconnect()
            startTranslationXAnimator(false)
        }else{
            updateServerInfo()
            if (getNetStatus()==1){
                toast("Please check your network")
                canClick=true
                return
            }
            if (VpnService.prepare(this) != null) {
                permission = true
                registerResult.launch(null)
                return
            }

            startConnectServer()
        }
    }

    private fun startConnectServer(){
        hideGuideView()
        updateConnectingView()
        TimeUtil.resetTime()
        ConnectUtil.connect()
        startTranslationXAnimator(true)
    }

    private fun startTranslationXAnimator(connect: Boolean){
        translationX= ValueAnimator.ofInt(0, 100).apply {
            duration=3000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                iv_connecting.translationX=getTranslationY(connect, pro)
//                val duration = (10 * (pro / 100.0F)).toInt()
            }
            doOnEnd {
                updateConnectResultView(connect)
                toResultAc(connect)
            }
            start()
        }
    }

    private fun updateConnectResultView(connect: Boolean){
        if (ConnectUtil.connectServerSuccess(connect)){
            if (connect){
                updateConnectedView()
            }else{
                updateStoppedView()
                updateServerInfo()
            }
        }else{
            updateStoppedView()
        }
    }

    private fun toResultAc(connect: Boolean){
        if(RegisterAc.isFront){
            if (!ConnectUtil.connectServerSuccess(connect)){
                toast(if (connect) "Connect Fail" else "Disconnect Fail")
            }
            startActivity(Intent(this,ResultAc::class.java).apply {
                putExtra("connect",connect)
            })
        }
        canClick=true
    }
    private fun getTranslationY(connect: Boolean,pro:Int):Float{
        val fl = maxPx/ 100F * pro
        return if (connect) fl else maxPx-fl
    }

    private fun updateConnectingView(){
        llc_connect.show(false)
        llc_connected.show(false)
        iv_connecting.show(true)
        iv_center.setImageResource(R.drawable.center_connect)
    }

    private fun updateConnectedView(){
        llc_connect.show(false)
        llc_connected.show(true)
        iv_connecting.show(false)
        iv_center.setImageResource(R.drawable.center_connected)
    }

    private fun updateStoppedView(){
        llc_connect.show(true)
        llc_connected.show(false)
        iv_connecting.show(false)
        tv_connect_time.text="00:00:00"
        iv_center.setImageResource(R.drawable.center_connect)
    }

    private fun updateServerInfo(){
        val currentServer = ConnectUtil.currentServer
        tv_name.text=currentServer.country
        iv_logo.setImageResource(getLogo(currentServer.country))
    }

    override fun connectTime(time: String) {
        tv_connect_time.text=time
    }

    override fun connectSuccess() {
        updateConnectedView()
    }

    override fun disconnectSuccess() {
        if(canClick){
            updateStoppedView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==1000){
            when(data?.getStringExtra("back")){
                "new_vpn_dis"->{
                    clickConnectView()
                }
                "new_vpn_con"->{
                    updateServerInfo()
                    clickConnectView()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(guideIsShowing()){
            hideGuideView()
            return
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ConnectUtil.onDestroy()
        TimeUtil.setInterface(this)
    }

    private fun guideIsShowing()=guide_lottie_view.visibility==View.VISIBLE

    private fun hideGuideView(){
        guide_lottie_view.show(false)
        guide_view.show(false)
    }
}