package com.demo.newvpn.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.SizeUtils
import com.demo.newvpn.*
import com.demo.newvpn.admob.LoadAd
import com.demo.newvpn.admob.ShowNativeAd
import com.demo.newvpn.admob.ShowOpenAd
import com.demo.newvpn.call.IConnectCall
import com.demo.newvpn.call.ITimeCall
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.server.ConnectUtil
import com.demo.newvpn.server.ConnectUtil.connectServerSuccess
import com.demo.newvpn.server.TimeUtil
import com.demo.newvpn.util.AdLimitManager
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.drawer_home.*
import kotlinx.coroutines.*
import java.lang.Exception

class HomeAc :BaseAc(), ITimeCall, IConnectCall, RegisterAc.IAppHome {
    private var lastClickTime=0L
    private var canClick=true
    private var permission=false
    private var connect=false
    private var time=0
    private var connectJob:Job?=null
    private val maxPx= SizeUtils.dp2px(104F)
    private var translationX: ValueAnimator?=null
    private val showConnectAd by lazy { ShowOpenAd(LocalConf.CONNECT,this) }
    private val showHomeBottomAd by lazy { ShowNativeAd(LocalConf.HOME_BOTTOM,this) }

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
        RegisterAc.setAppHome(this)
        ConnectUtil.init(this,this)
        setClick()
        if(ConnectUtil.isConnected()){
            hideGuideView()
        }
    }

    private fun setClick(){
        view_connect.setOnClickListener { clickConnectView() }
        llc_server_list.setOnClickListener {
            val time = System.currentTimeMillis() - lastClickTime
            if(canClick&&!drawer_layout.isOpen&&!guideIsShowing()&&time>500){
                lastClickTime=System.currentTimeMillis()
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
            return
        }
        LoadAd.load(LocalConf.CONNECT)
        LoadAd.load(LocalConf.RESULT_BOTTOM)
        if(!canClick){
            return
        }
        canClick=false
        if(ConnectUtil.isConnected()){
            updateConnectingView()
//            ConnectUtil.disconnect()
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
//        ConnectUtil.connect()
        startTranslationXAnimator(true)
    }

    private fun startTranslationXAnimator(connect: Boolean){
        this.connect=connect
        translationX= ValueAnimator.ofInt(0, 100).apply {
            duration=10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val pro = it.animatedValue as Int
                iv_connecting.translationX=getTranslationY(connect, pro)
            }
            start()
        }

        connectJob=GlobalScope.launch {
            time=0
            while (true){
                if (!isActive){
                    break
                }
                delay(1000L)
                time++
                if (time==3){
                    if(connect){
                        ConnectUtil.connect()
                    }else{
                        ConnectUtil.disconnect()
                    }
                }
                withContext(Dispatchers.Main){
                    if(time in 3..9){
                        if(connectServerSuccess(connect)){
                            if (null!=LoadAd.getAdByType(LocalConf.CONNECT)){
                                cancel()
                                stopTranslator()
                                showConnectAd.showOpenAd(
                                    showing = {
                                        updateConnectResultView(connect,jump=false)
                                    },
                                    close = {
                                        updateConnectResultView(connect)
                                    }
                                )
                            }else{
                                if(AdLimitManager.hasLimit()){
                                    cancel()
                                    stopTranslator()
                                    updateConnectResultView(connect)
                                }
                            }
                        }
                    }else if (time>=10){
                        cancel()
                        stopTranslator()
                        updateConnectResultView(connect)
                    }
                }

            }
        }
    }

    private fun updateConnectResultView(connect: Boolean,jump:Boolean=true){
        iv_connecting.translationX=getTranslationY(connect, 100)
        if (connectServerSuccess(connect)){
            if (connect){
                updateConnectedView()
            }else{
                updateStoppedView()
                updateServerInfo()
            }
        }else{
            updateStoppedView()
        }
        toResultAc(connect,jump)
    }

    private fun toResultAc(connect: Boolean,jump:Boolean=true){
        if(ConnectUtil.isConnected()){
            TimeUtil.start()
        }
        if(RegisterAc.isFront&&jump){
            if (!connectServerSuccess(connect)){
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
        TimeUtil.start()
    }

    override fun disconnectSuccess() {
        if(canClick){
            updateStoppedView()
        }
    }

    private fun stopTranslator(){
        translationX?.removeAllUpdateListeners()
        translationX?.cancel()
        connectJob?.cancel()
        connectJob=null
    }

    override fun onHome(home: Boolean) {
        if(time<=2){
            canClick = true
            stopTranslator()
            if (connect) {
                updateStoppedView()
            } else {
                updateConnectedView()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==313){
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
        if (!canClick){
            return
        }
        if(guideIsShowing()){
            hideGuideView()
            return
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        showHomeBottomAd.showNativeAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTranslator()
        ConnectUtil.onDestroy()
        TimeUtil.setInterface(this)
        RegisterAc.setAppHome(null)
        showHomeBottomAd.stopNativeAd()
    }

    private fun guideIsShowing()=guide_lottie_view.visibility==View.VISIBLE

    private fun hideGuideView(){
        guide_lottie_view.show(false)
        guide_view.show(false)
    }
}