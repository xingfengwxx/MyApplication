package com.example.myapplication

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.PermissionUtils.FullCallback
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        initView()
        initProgressBar()
        initTest()

        writeShareData()


//        getShareData();

        Log.d("wxx", "deviceId=" + DeviceUtils.getUniqueDeviceId())

        val id = "a1332"
        val uuid = "android" + UUID.nameUUIDFromBytes(id.toByteArray()).toString().replace("-", "")
        Log.d("wxx", "UUID: $uuid")
    }

    private fun checkPermissions() {
        PermissionUtils.permissionGroup(PermissionConstants.STORAGE)
            .callback(object : FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                }

                override fun onDenied(
                    deniedForever: MutableList<String>,
                    denied: MutableList<String>
                ) {
                }

            })
            .request()
    }

    private fun initView() {
        binding.btnDownload.setOnClickListener {
//            download()

            val transparentDialog = TransparentDialog(this)
            transparentDialog.show()
        }

        binding.btnJump.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }

        val desc = getString(R.string.playmods_260_space_dlg_vip_desc_experience, "3")
        binding.tvText.text = Html.fromHtml(desc)
    }

    private fun download() {
        val apkUrl =
            "https://gg-resource.playmods.net/prd/package/b82fa1bb-cd5d-4056-812b-3d989153e98f.apk"
        val request = DownloadManager.Request(Uri.parse(apkUrl))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle("My File")
            .setDescription("Downloading")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "GTA.apk")

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)

        val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                // 下载完成的处理逻辑
                ToastUtils.showShort("下载完成：$downloadID")
            }
        }
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private var progress = 0
    private fun initProgressBar() {
        Thread() {
            while (progress < 100) {
                Thread.sleep(500)
                progress++
                runOnUiThread {
//                binding.customProgressBar.setProgress()
                    binding.btnDownloadProgress.setProgress(progress.toFloat())
                }
            }
        }.start()
    }

    private fun initTest() {
        try {
            val packageManager = packageManager
            val packageInfo = packageManager.getPackageInfo("com.studio27.MelonPlayground", 0)
            // 在这里使用packageInfo获取应用信息
            LogUtils.i("packageInfo: ${packageInfo.packageName}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            // 处理找不到包名的情况
        }

        val pkgName = "com.studio27.MelonPlayground"
        LogUtils.i("isInstall: ${AppUtils.isAppInstalled(pkgName)}")
    }

    /**
     * 测试多进程共享数据
     *
     */
    private fun writeShareData() {
        // 获取SharedPreferences实例
        val sharedPreferences = getSharedPreferences("shared_data", MODE_PRIVATE)
        // 编辑SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("shared_key", "Hello, other app! This data is shared.")
        editor.apply()
        LogUtils.i("写入共享数据")
    }
}