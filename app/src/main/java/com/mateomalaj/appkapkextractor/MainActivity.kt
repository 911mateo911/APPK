package com.mateomalaj.appkapkextractor

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.adapters.ApkListAdapter
import com.mateomalaj.appkapkextractor.adapters.FragmentAdapter
import com.mateomalaj.appkapkextractor.fragments.FragmentGoogleApps
import com.mateomalaj.appkapkextractor.fragments.FragmentInstalledApps
import com.mateomalaj.appkapkextractor.fragments.FragmentSystemApps
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_google_apps.*
import kotlinx.android.synthetic.main.fragment_installed_apps.*
import kotlinx.android.synthetic.main.fragment_system_apps.*
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
        val adapter =FragmentAdapter(supportFragmentManager)
        adapter.addfragment(FragmentInstalledApps(), "User Installed")
        adapter.addfragment(FragmentGoogleApps(), "Google")
        adapter.addfragment(FragmentSystemApps(), "System")
        viewPager.adapter = adapter
        tablayout.setViewPager(viewPager)
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
                    apklistSystem.add(userApk)
                } else {
                    if (userApk.packagename!!.startsWith("com.google")) {
                        apklistGoogle.add(userApk)
                    } else {
                        apklistInstalled.add(userApk)
                    }
                }
            }
            uiThread {
                val adapterinstalled = ApkListAdapter(apklistInstalled,MainActivity())
                val adaptergoogle = ApkListAdapter(apklistGoogle,MainActivity())
                val adaptersystem = ApkListAdapter(apklistSystem,MainActivity())
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

    override fun uninstallApp(uri: Uri) {
        val uninstallIntent = Intent(Intent.ACTION_DELETE)
        uninstallIntent.setData(uri)
        Log.d("mainactivity", uri.toString())
        uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(uninstallIntent)
    }

    override fun getpermissions(kush: String): Array<String>{
        var tedhenat : Array<String>? = null
        try {
            val pm = packageManager
            val p = pm.getPackageInfo(kush, PackageManager.GET_PERMISSIONS)
            val permissions : Array<String> = p.requestedPermissions
            tedhenat = permissions
        } catch (e: Exception){}
        return tedhenat!!
    }


    override fun launchandplay(thing: Int, who: String) {
        when(thing) {
            R.id.launch_tv -> {
                try {
                    startActivity(packageManager.getLaunchIntentForPackage(who))
                } catch (e: Exception) {
                    Snackbar.make(find(android.R.id.content), "Cannot open this app", Snackbar.LENGTH_SHORT).show()
                }
            }
            R.id.openplay_tv -> {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$who")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$who")))
                }
            }
        }
    }
}