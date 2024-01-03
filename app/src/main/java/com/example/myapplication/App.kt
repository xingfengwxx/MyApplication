package com.example.myapplication

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * author : 王星星
 * date : 2023/4/23 17:33
 * email : 1099420259@qq.com
 * description :
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}