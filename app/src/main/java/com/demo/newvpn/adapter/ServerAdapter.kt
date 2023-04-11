package com.demo.newvpn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.newvpn.R
import com.demo.newvpn.bean.CountryBean
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.getLogo
import com.demo.newvpn.server.ConnectUtil
import kotlinx.android.synthetic.main.item_server.view.*

class ServerAdapter(
    private val context: Context,
    private val list:ArrayList<CountryBean>,
    private val click:(bean:CountryBean)->Unit
):RecyclerView.Adapter<ServerAdapter.ServerView>() {

    inner class ServerView(view:View):RecyclerView.ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerView {
        return ServerView(LayoutInflater.from(context).inflate(R.layout.item_server,parent,false))
    }

    override fun onBindViewHolder(holder: ServerView, position: Int) {
        with(holder.itemView){
            val serverBean = list[position]
            tv_name.text=if (serverBean.fast()){
                serverBean.countryName
            }else{
                "${serverBean.countryName}-${serverBean.cityName}-${serverBean.cityId}"
            }
            iv_logo.setImageResource(getLogo(serverBean.countryName))
            val b = if(serverBean.isLocal)
                ConnectUtil.currentServer.country==serverBean.countryName
            else
                ConnectUtil.currentServer.cityId==serverBean.cityId
            iv_sel.isSelected=b
            item_layout.isSelected=b
        }
    }

    override fun getItemCount(): Int = list.size
}