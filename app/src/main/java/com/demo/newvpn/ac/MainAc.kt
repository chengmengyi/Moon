package com.demo.newvpn.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.newvpn.BaseAc
import com.demo.newvpn.R
import com.demo.newvpn.admob.LoadAd
import com.demo.newvpn.admob.ShowOpenAd
import com.demo.newvpn.conf.LocalConf
import com.demo.newvpn.util.AdLimitManager
import kotlinx.android.synthetic.main.activity_main.*

class MainAc : BaseAc() {
    private var animator: ValueAnimator?=null
    private val showOpenAd by lazy { ShowOpenAd(LocalConf.OPEN,this) }

    override fun layout(): Int = R.layout.activity_main

    override fun initView() {
        AdLimitManager.resetRefresh()
        AdLimitManager.readNum()
        LoadAd.preAllAd()
        startAnimator()
    }

    private fun startAnimator(){
        animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if(pro in 2..9){
                    showOpenAd.showOpenAd(
                        showing = {
                            stopAnimator()
                            progress_view.progress = 100
                        },
                        close = {
                            toHomeAc()
                        }
                    )
                }else if (pro>=10){
                    toHomeAc()
                }
            }
            start()
        }
    }

    private fun toHomeAc(){
        startActivity(Intent(this,HomeAc::class.java))
        finish()
    }

    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
    }

    override fun onResume() {
        super.onResume()
        if (animator?.isPaused==true){
            animator?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        animator?.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }
}