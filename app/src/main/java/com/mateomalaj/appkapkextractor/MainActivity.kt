package com.mateomalaj.appkapkextractor

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.adapters.ApkListAdapter
import com.mateomalaj.appkapkextractor.adapters.FragmentAdapter
import com.mateomalaj.appkapkextractor.fragments.FragmentGoogleApps
import com.mateomalaj.appkapkextractor.fragments.FragmentInstalledApps
import com.mateomalaj.appkapkextractor.fragments.FragmentSystemApps
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

val apklistInstalled = ArrayList<ApkModel>()
val apklistGoogle = ArrayList<ApkModel>()
val apklistSystem = ArrayList<ApkModel>()

class MainActivity : AppCompatActivity(), ApkListAdapter.FunctionsOnMain {
    lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressbar = find(R.id.progressBar)
        Tools.checkPermission(this)
        loadApk()
    }

    private fun setuptabs() {
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.addfragment(FragmentInstalledApps(),"User Installed")
        adapter.addfragment(FragmentGoogleApps(),"Google")
        adapter.addfragment(FragmentSystemApps(),"System")
        val viewPager = find<ViewPager>(R.id.viewPager)
        val bottombar = findViewById<AnimatedBottomBar>(R.id.bottombar)
        viewPager.adapter = adapter
        bottombar.setupWithViewPager(viewPager)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun loadApk() {
        doAsync {
            val allpackages: List<PackageInfo> =
                    packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            allpackages.forEach {
                val applicationInfo: ApplicationInfo = it.applicationInfo
                val userApk = ApkModel(
                    applicationInfo,
                    packageManager.getApplicationLabel(applicationInfo).toString(),
                    it.packageName,
                    it.versionName,
                    it.firstInstallTime,
                    it.lastUpdateTime
                )
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
                    if (userApk.packagename!!.startsWith("com.google")) {
                        apklistGoogle.add(userApk)
                    } else {
                        apklistSystem.add(userApk)
                    }
                } else {
                    apklistInstalled.add(userApk)
                }
            }
            uiThread {
                val adapterinstalled = ApkListAdapter(apklistInstalled, MainActivity())
                val adaptergoogle = ApkListAdapter(apklistGoogle, MainActivity())
                val adaptersystem = ApkListAdapter(apklistSystem, MainActivity())
                adapterinstalled.notifyDataSetChanged()
                adaptergoogle.notifyDataSetChanged()
                adaptersystem.notifyDataSetChanged()
                setuptabs()
                progressbar.visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            Tools.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Tools.makeAppDirectory()
                } else {
                    Snackbar.make(
                            find(android.R.id.content),
                            "Permission required to extract Apk",
                            Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getpermissions(kush: String): Array<String> {
        var permisionat: Array<String>
        val pm = packageManager
        val p = pm.getPackageInfo(kush, PackageManager.GET_PERMISSIONS)
        val persat = p.requestedPermissions
        if (persat != null) {
            permisionat = persat
        } else {
            permisionat = arrayOf("No permissions Found")
        }
        return permisionat
    }
}