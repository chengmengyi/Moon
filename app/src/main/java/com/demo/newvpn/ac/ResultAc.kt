package com.demo.newvpn.ac

import com.demo.newvpn.BaseAc
import com.demo.newvpn.R
import com.demo.newvpn.call.ITimeCall
import com.demo.newvpn.server.TimeUtil
import kotlinx.android.synthetic.main.activity_result.*

class ResultAc :BaseAc(), ITimeCall {
    private var connect=false

    override fun layout(): Int = R.layout.activity_result

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        connect=intent.getBooleanExtra("connect",false)
        tv_connect_time.isSelected=connect
        if(connect){
            tv_title.text="Connect success"
            tv_connect_status.text="Connected Successfully"
            TimeUtil.setInterface(this)
        }else{
            tv_connect_time.text=TimeUtil.getTotalTime()
        }
    }

    override fun connectTime(time: String) {
        tv_connect_time.text=time
    }

    override fun onDestroy() {
        super.onDestroy()
        if(connect){
            TimeUtil.setInterface(this)
        }
    }
}