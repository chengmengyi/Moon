package com.demo.newvpn.conf

import com.demo.newvpn.bean.CountryBean
import com.demo.newvpn.bean.ServerBean

object LocalConf {
    const val email="en2424l53@gmail.com"
    const val url="https://sites.google.com/view/moon-app/home"

    const val OPEN="moon_o"
    const val HOME_BOTTOM="n_moon_home"
    const val RESULT_BOTTOM="n_moon_buttom"
    const val CONNECT="i_moon_lin"
    const val BACK="i_moon_back"

    val localCountryList= arrayListOf(
        CountryBean(
            countryName = "Germany",
            cityName = "Dusseldorf",
            cityId = 0,
            isLocal = true
        )
    )


    val localServerList= arrayListOf(
        ServerBean(
            ip = "185.194.216.196",
            port = 5691,
            pwd = "9OuPVw#UBajyyzM",
            account = "chacha20-ietf-poly1305",
            country = "Germany",
            city = "Dusseldorf",
            cityId = 0,
            isLocal = true
        )
    )


    const val localAd="""{
    "max_s":"30",
    "max_c":"5",
    "moon_o":[
    {
           "moonfrom":"admob",
           "moonadid":"ca-app-pub-6337191878285963/1937976979",
           "moontype":"o",
           "moonsort":1
           }
    ],
    "n_moon_home":[
    {
           "moonfrom":"admob",
           "moonadid":"ca-app-pub-6337191878285963/3362739281",
           "moontype":"n",
           "moonsort":1
           }
    ],
    "n_moon_buttom":[
    {
           "moonfrom":"admob",
           "moonadid":"ca-app-pub-6337191878285963/9736575944",
           "moontype":"n",
           "moonsort":1
           }
    ],
    "i_moon_lin":[
    {
           "moonfrom":"admob",
           "moonadid":"ca-app-pub-6337191878285963/5797330934",
           "moontype":"i",
           "moonsort":1
           }
    ],
    "i_moon_back":[
    {
           "moonfrom":"admob",
           "moonadid":"ca-app-pub-6337191878285963/2297069628",
           "moontype":"i",
           "moonsort":1
           }
    ]
}"""

}