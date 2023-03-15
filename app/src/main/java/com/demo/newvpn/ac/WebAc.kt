package com.demo.newvpn.ac

import com.demo.newvpn.BaseAc
import com.demo.newvpn.R
import com.demo.newvpn.conf.LocalConf
import kotlinx.android.synthetic.main.activity_web.*

class WebAc :BaseAc() {
    override fun layout(): Int = R.layout.activity_web

    override fun initView() {
        immersionBar.statusBarView(top).init()
        iv_back.setOnClickListener { finish() }
        web_view.apply {
            settings.javaScriptEnabled=true
            loadUrl(LocalConf.url)
        }
    }
}