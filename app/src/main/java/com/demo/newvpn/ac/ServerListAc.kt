package com.demo.newvpn.ac

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.newvpn.BaseAc
import com.demo.newvpn.R
import com.demo.newvpn.adapter.ServerAdapter
import com.demo.newvpn.admob.LoadAd
import com.demo.newvpn.admob.ShowOpenAd
import com.demo.newvpn.bean.CountryBean
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.server.ConnectUtil
import com.demo.newvpn.tba.OkUtil
import kotlinx.android.synthetic.main.activity_server_list.*

class ServerListAc:BaseAc() {
    private val showBackAd by lazy { ShowOpenAd(LocalConf.BACK,this) }

    override fun layout(): Int = R.layout.activity_server_list

    override fun initView() {
        immersionBar.statusBarView(top).init()
        LoadAd.load(LocalConf.BACK)
        val list = intent.getSerializableExtra("list") as ArrayList<CountryBean>
        list.add(0, CountryBean(isLocal = list.first().isLocal))
        rv_server.apply {
            layoutManager=LinearLayoutManager(this@ServerListAc)
            adapter=ServerAdapter(this@ServerListAc,list){
                if(it.isLocal){
                    if(it.fast()){
                        click(ServerBean(isLocal = true))
                    }else{
                        val filter = LocalConf.localServerList.filter { bean -> bean.country == it.countryName }
                        if(filter.isEmpty()){
                            click(LocalConf.localServerList.random())
                        }else{
                            click(filter.first())
                        }
                    }
                }else{
                    OkUtil.getServerInfoByCityId(supportFragmentManager,it.cityId){ serverBean->
                        click(serverBean)
                    }
                }
            }
        }
        iv_back.setOnClickListener { onBackPressed() }
    }

    private fun click(serverBean: ServerBean){
        val current = ConnectUtil.currentServer
        val connected = ConnectUtil.isConnected()
        if(connected&&current.ip!=serverBean.ip){
            AlertDialog.Builder(this).apply {
                setMessage("If you want to connect to another VPN, you need to disconnect the current connection first. Do you want to disconnect the current connection?")
                setPositiveButton("sure") { _, _ ->
                    chooseBackHome(serverBean,"new_vpn_dis")
                }
                setNegativeButton("cancel",null)
                show()
            }
        }else{
            if (connected){
                chooseBackHome(serverBean,"")
            }else{
                chooseBackHome(serverBean,"new_vpn_con")
            }
        }
    }

    private fun chooseBackHome(serverBean: ServerBean,result:String){
        ConnectUtil.currentServer=serverBean
        setResult(313, Intent().apply {
            putExtra("back",result)
        })
        finish()
    }

    override fun onBackPressed() {
        showBackAd.showOpenAd(
            back = true,
            showing = {},
            close = {
                finish()
            }
        )
    }
}