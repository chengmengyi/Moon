package com.demo.newvpn.server

import com.demo.newvpn.call.ITimeCall
import kotlinx.coroutines.*
import java.lang.Exception

object TimeUtil {
    private var time=0L
    private var timeJob: Job?=null
    private val interfaceList= arrayListOf<ITimeCall>()

    fun setInterface(iTimeCall: ITimeCall){
        if(interfaceList.contains(iTimeCall)){
            interfaceList.remove(iTimeCall)
        }else{
            interfaceList.add(iTimeCall)
        }
    }

    fun resetTime(){
        time=0L
    }

    fun start(){
        if (null!= timeJob) return
        timeJob = GlobalScope.launch(Dispatchers.Main) {
            while (null!=timeJob) {
                interfaceList.forEach { it.connectTime(transTime(time)) }
                time++
                delay(1000L)
            }
        }
    }

    fun end(){
        timeJob?.cancel()
        timeJob=null
    }

    fun getTotalTime()=transTime(time)

    private fun transTime(t:Long):String{
        try {
            val shi=t/3600
            val fen= (t % 3600) / 60
            val miao= (t % 3600) % 60
            val s=if (shi<10) "0${shi}" else shi
            val f=if (fen<10) "0${fen}" else fen
            val m=if (miao<10) "0${miao}" else miao
            return "${s}:${f}:${m}"
        }catch (e: Exception){}
        return "00:00:00"
    }
}