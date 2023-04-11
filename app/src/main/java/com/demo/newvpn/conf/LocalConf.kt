package com.demo.newvpn.conf

object LocalConf {

    const val OPEN="moon_o"
    const val HOME_BOTTOM="n_moon_home"
    const val RESULT_BOTTOM="n_moon_buttom"
    const val CONNECT="i_moon_lin"
    const val BACK="i_moon_back"

    const val email="en2424l53@gmail.com"
    const val url="https://sites.google.com/view/moon-app/home"

    const val localServer="""[
  {
    "moon_getpwd": "9OuPVw#UBajyyzM",
    "moon_getaccount": "chacha20-ietf-poly1305",
    "moon_getport": 5691,
    "moon_getcountry": "Germany",
    "moon_getcity": "Dusseldorf",
    "moon_getip": "185.194.216.196"
  }
]"""

    const val localAd="""
{
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