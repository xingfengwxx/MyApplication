package com.example.myapplication

/**
 * author : 王星星
 * date : 2023/8/31 10:07
 * email : 1099420259@qq.com
 * description :
 */
import android.app.Dialog
import android.content.Context
import android.os.Bundle

class TransparentDialog(context: Context) : Dialog(context, R.style.TransparentDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_layout)
    }
}
