package com.demo.newvpn.conf

object LocalConf {
    const val email=""
    const val url=""

    const val OPEN="moon_o"
    const val HOME_BOTTOM="n_moon_home"
    const val RESULT_BOTTOM="n_moon_buttom"
    const val CONNECT="i_moon_lin"
    const val BACK="i_moon_back"

    const val localServer="""[
    {
        "moon_getpwd":"123456",
        "moon_getaccount":"chacha20-ietf-poly1305",
        "moon_getport":100,
        "moon_getcountry":"Japan",
        "moon_getcity":"Tokyo",
        "moon_getip":"100.223.52.0"
    },
    {
        "moon_getpwd":"123456",
        "moon_getaccount":"chacha20-ietf-poly1305",
        "moon_getport":100,
        "moon_getcountry":"UnitedStates",
        "moon_getcity":"NewYork",
        "moon_getip":"100.223.52.1"
    }
]"""

    const val localAd="""{
    "max_c":15,
    "max_s":50,
    "moon_o":[
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/3419835294",
            "moontype":"o",
            "moonsort":2
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/3419835294A",
            "moontype":"o",
            "moonsort":3
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/8691691433",
            "moontype":"i",
            "moonsort":1
        }
    ],
    "n_moon_home":[
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/2247696110",
            "moontype":"n",
            "moonsort":2
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/2247696110A",
            "moontype":"n",
            "moonsort":3
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/1044960115",
            "moontype":"n",
            "moonsort":1
        }
    ],
    "n_moon_buttom":[
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/2247696110",
            "moontype":"n",
            "moonsort":1
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/2247696110A",
            "moontype":"n",
            "moonsort":3
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/1044960115",
            "moontype":"n",
            "moonsort":2
        }
    ],
    "i_moon_lin":[
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/1033173712",
            "moontype":"i",
            "moonsort":2
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/8691691433",
            "moontype":"i",
            "moonsort":1
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/8691691433XX",
            "moontype":"i",
            "moonsort":3
        }
    ],
    "i_moon_back":[
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/1033173712",
            "moontype":"i",
            "moonsort":1
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/8691691433",
            "moontype":"i",
            "moonsort":2
        },
        {
            "moonfrom":"admob",
            "moonadid":"ca-app-pub-3940256099942544/8691691433XX",
            "moontype":"i",
            "moonsort":3
        }
    ]
}"""

}