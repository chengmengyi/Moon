package com.demo.newvpn.tba

import android.util.Base64
import android.webkit.WebView
import androidx.fragment.app.FragmentManager
import com.demo.newvpn.*
import com.demo.newvpn.bean.CountryBean
import com.demo.newvpn.bean.ServerBean
import com.demo.newvpn.conf.FireConf
import com.demo.newvpn.conf.LocalConf.localCountryList
import com.demo.newvpn.conf.LocalConf.localServerList
import com.demo.newvpn.dialog.LoadingDialog
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.random.Random

object OkUtil {
    var ip=""
    var countryCode=""
    private val countryList= arrayListOf<CountryBean>()
    private const val TBA_URL="https://test-influx.mooninternet.net/bodleian/set/mutineer/alfonso"
    private const val SERVER_URL="https://test.fastmoon.link"

    fun requestIp(callback:()->Unit){
        if(ip.isNotEmpty()){
            callback.invoke()
            return
        }
        OkGo.get<String>("https://ipapi.co/json")
            .headers("User-Agent", WebView(mMoonApp).settings.userAgentString)
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    runCatching {
                        val jsonObject = JSONObject(response?.body()?.toString())
                        countryCode=jsonObject.optString("country_code")
                        ip=jsonObject.optString("ip")
                    }
                    callback.invoke()
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    callback.invoke()
                }
            })
    }

    fun uploadEvent(jsonObject:JSONObject,install:Boolean=false){
        val path="$TBA_URL?curtsey=$ip&demigod=${TbaCommon.getSystemLanguage()}&linear=${TbaCommon.getDeviceModel()}&lotion=${TbaCommon.getZoneOffset()}&manley=${TbaCommon.getOperator(mMoonApp)}"
        moonLogEvent(path)
        moonLogEvent(jsonObject.toString())

        OkGo.post<String>(path)
            .retryCount(3)
            .headers("content-type","application/json")
            .headers("geiger", System.currentTimeMillis().toString())
            .headers("rutabaga", TbaCommon.getManufacturer())
            .headers("demigod", TbaCommon.getSystemLanguage())
            .upJson(jsonObject)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    if (install){
                        if (jsonObject.getJSONObject("gritty").optString("aghast").isEmpty()){
                            saveNoReferrerTag()
                        }else{
                            saveHasReferrerTag()
                        }
                    }
                    moonLogEvent("=onSuccess==${response?.body()?.toString()}==")
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    moonLogEvent("=onError==${response?.body()?.toString()}==")
                }
            })
    }


    fun getServerList(manager: FragmentManager, callback: (list:ArrayList<CountryBean>) -> Unit){
        if(countryList.isEmpty()){
            var loadingDialog= LoadingDialog()
            loadingDialog.show(manager,"LoadingDialog")
            OkGo.get<String>("$SERVER_URL/qpn/ssci/")
                .retryCount(3)
                .headers("APL", TbaCommon.getOsCountry())
                .headers("XNS", mMoonApp.packageName)
                .headers("AKD",TbaCommon.getAndroidId(mMoonApp))
                .execute(object : StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        loadingDialog.dismiss()
                        parseServerListJson(decodeServerStr(response?.body()?.toString()?:""))
                        if(countryList.isEmpty()){
                            callback.invoke(localCountryList)
                        }else{
                            callback.invoke(countryList)
                        }
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        moonLogEvent("==onError==${response?.body()?.toString()?:""}")
                        loadingDialog.dismiss()
                        callback.invoke(localCountryList)
                    }
                })
        }else{
            callback.invoke(countryList)
        }
    }

    fun getServerInfoByCityId(manager: FragmentManager?,cityId:Int,callback: (bean: ServerBean) -> Unit){
        val jsonObject = JSONObject()
        jsonObject.put("ObT","ss")
        if(cityId!=0){
            jsonObject.put("xNm",cityId)
        }
        var loadingDialog=LoadingDialog()
        manager?.let {
            loadingDialog.show(it,"LoadingDialog")
        }

        OkGo.post<String>("$SERVER_URL/qpn/ssci/")
            .retryCount(3)
            .headers("APL", TbaCommon.getOsCountry())
            .headers("XNS", mMoonApp.packageName)
            .headers("AKD",TbaCommon.getAndroidId(mMoonApp))
            .upJson(jsonObject)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    manager?.let { loadingDialog.dismiss() }
                    val serverInfo = parseServerInfo(decodeServerStr(response?.body()?.toString() ?: ""), cityId)
                    if(null==serverInfo){
                        callback.invoke(localServerList.random())
                    }else{
                        callback.invoke(serverInfo)
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    manager?.let { loadingDialog.dismiss() }
                    callback.invoke(localServerList.random())
                }
            })
    }

    private fun parseServerListJson(string: String){
        runCatching {
            moonLogEvent(string)
            val jsonObject = JSONObject(string)
            if(jsonObject.optInt("code")==200){
                val jsonArray = jsonObject.getJSONObject("data").getJSONArray("PUHCbzz")
                countryList.clear()
                for (index in 0 until jsonArray.length()){
                    val json = jsonArray.getJSONObject(index)
                    val topic = json.getJSONArray("zvXZLwLt")
                    for (index2 in 0 until topic.length()){
                        val json2 = topic.getJSONObject(index2)
                        countryList.add(
                            CountryBean(
                                countryName = json.optString("BbRv"),
                                cityId = json2.optInt("xNm"),
                                cityName = json2.optString("aRSWuegwmp")
                            )
                        )
                    }
                }
            }
        }
    }

    private fun parseServerInfo(string: String,cityId: Int):ServerBean?{
        moonLogEvent("serverinfo:$string")
//        {
//            "code":200,
//            "msg":"\u83b7\u53d6\u670d\u52a1\u5668\u6210\u529f",
//            "data":[
//            {
//                "rYPyY":"ss",
//                "QClHnpA":"1.1.1.1",
//                "rvBZCtHxhc":1,
//                "MYNTZeU":"root",
//                "mXKVT":"kRoqKuI4Wq0VpPxb2k2i",
//                "eFHTxD":"chacha20-ietf-poly1305",
//                "ZCENAlUDiR":"Los Angeles",
//                "NKtkXoFac":"United States",
//                "OzU":"US"
//            }
//            ]
//        }
        runCatching {
            val jsonObject = JSONObject(string)
            if(jsonObject.optInt("code")==200){
                val list= arrayListOf<ServerBean>()
                val jsonArray = jsonObject.getJSONArray("data")
                for (index in 0 until jsonArray.length()){
                    val json = jsonArray.getJSONObject(index)
                    list.add(
                        ServerBean(
                            ip = json.optString("VdlKaXZLuN"),
                            port = json.optInt("ITvoz"),
                            pwd = json.optString("oToPieA"),
                            account = json.optString("umaKFpMosY"),
                            country = json.optString("BbRv"),
                            city = json.optString("RHRpyOl"),
                            cityId = cityId
                        )
                    )
                }
                if(list.isNotEmpty()){
                    val serverBean = list.random(Random(System.currentTimeMillis()))
                    serverBean.writeServerId()
                    return serverBean
                }
            }
        }
        return null
    }

    private fun decodeServerStr(string: String):String{
        runCatching {
            val buffer = StringBuffer()
            for (index in string.indices) {
                if(index%2!=0){
                    buffer.append(string[index])
                }
            }
            return String(Base64.decode(buffer.toString(), Base64.DEFAULT))
        }
        return ""
    }

    fun checkUserCloak(){
        GlobalScope.launch {
            val path="https://blur.mooninternet.net/magog/winnetka/snare?bilayer=${TbaCommon.getDistinctId(mMoonApp)}&curtsey=$ip&geiger=${System.currentTimeMillis()}&linear=${TbaCommon.getDeviceModel()}&standeth=${TbaCommon.getBundleId(mMoonApp)}&lumbago=${TbaCommon.getOsVersion()}&foist=${TbaCommon.getGaid(
                mMoonApp)}&library=${TbaCommon.getAndroidId(mMoonApp)}&gs=${TbaCommon.getOsVersion()}&reprisal=${TbaCommon.getAppVersion(
                mMoonApp)}&manley=${TbaCommon.getOperator(mMoonApp)}"
            moonLogEvent(path)
            OkGo.get<String>(path)
                .retryCount(3)
                .headers("geiger", System.currentTimeMillis().toString())
                .headers("rutabaga", TbaCommon.getManufacturer())
                .headers("demigod", TbaCommon.getSystemLanguage())
                .execute(object :StringCallback(){
                    override fun onSuccess(response: Response<String>?) {
                        moonLogEvent("=onSuccess==checkUserCloak==${response?.body()?.toString()}==")
                        FireConf.cloak=response?.body()?.toString()=="quanta"
                    }

                    override fun onError(response: Response<String>?) {
                        super.onError(response)
                        moonLogEvent("=onError==checkUserCloak==${response?.body()?.toString()}==")
                    }
                })
        }
    }
}