package com.mateomalaj.appkapkextractor

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.adapters.ApkListAdapter
import com.mateomalaj.appkapkextractor.adapters.appig
import com.mateomalaj.appkapkextractor.adapters.whichapp
import com.mateomalaj.appkapkextractor.fragments.FragmentGoogleApps
import com.mateomalaj.appkapkextractor.fragments.FragmentInstalledApps
import com.mateomalaj.appkapkextractor.fragments.FragmentSystemApps
import layout.transitions.library.Slide
import layout.transitions.library.Transitions
import org.jetbrains.anko.find
import render.animations.*
import java.text.SimpleDateFormat
import java.util.*

// nqs nje app eshte fshire
var apackage = ""

class PackageAttributes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_attributes)

        // getting intent bundle from put extra
        val b: Bundle? = intent.extras

        // initializing buttons and views
        val icon_iv = find<ImageView>(R.id.appicon)
        val name_tv = find<TextView>(R.id.appname)
        val pkg_tv = find<TextView>(R.id.apppkg)
        val install_tv = find<TextView>(R.id.dateinstalledtxt)
        val lupdated_tv = find<TextView>(R.id.lastupdatedtxt)
        val apksize_tv = find<TextView>(R.id.apksizetxt)
        val permissions_btn = find<CardView>(R.id.permissionbtn)
        val sharebtn = find<CardView>(R.id.sharebtn)
        val uninstallbtn = find<CardView>(R.id.uninstallbtn)
        val appdetailsbtn = find<CardView>(R.id.appdetailsbtn)
        val openinplay = find<CardView>(R.id.openplaybtn)
        val launchbtn = find<TextView>(R.id.launchbtn)
        val extracttext = find<TextView>(R.id.extracttext)
        val backarrow = find<ImageView>(R.id.backarrow)

        //from library to dont leak memory
        bindProgressButton(extracttext)

        // animations
        animations(icon_iv, 300)
        animations(name_tv, 320)
        animations(pkg_tv, 340)
        animations(uninstallbtn, 360)
        animations(sharebtn, 360)
        animations(appdetailsbtn, 380)
        animations(permissions_btn, 400)
        animations(openinplay, 420)

        // initializing bundle from but extra
        val appname = b?.getString("appname")
        val apppkg = b?.getString("apppkg")
        val dateinstalled = b?.getLong("dateinstalled")
        val lastupdated = b?.getLong("lastupdated")
        val apksize = b?.getString("apksize")
        val permissions = b?.getStringArray("permissions")

        // onclick listeners
        uninstallbtn.setOnClickListener {
            val uninstallIntent = Intent(Intent.ACTION_DELETE)
            uninstallIntent.data = Uri.parse("package:" + apppkg)
            uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            startActivity(uninstallIntent)
            if (apppkg!!.startsWith("com.google") || apppkg.startsWith("com.android")){
                val rootview: View = (this).window.decorView.findViewById(android.R.id.content)
                Snackbar.make(rootview, "Cannot uninstall Google/System apps", Snackbar.LENGTH_LONG).show()
            }
        }

        openinplay.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$apppkg")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$apppkg")))
            }
        }

        backarrow.setOnClickListener {
            val transition = Transitions(this)
            transition.setAnimation(Slide().InDown())
            if (ifdeleted() == true) {
                apklistSystem.clear()
                apklistGoogle.clear()
                apklistInstalled.clear()
                val intent = Intent(this,MainActivity()::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                super.onBackPressed()
                transition.setAnimation(Slide().InDown())
            }
        }

        permissions_btn.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.permissiondialog)
            dialog.setCanceledOnTouchOutside(true)
            val texti = dialog.find<TextView>(R.id.textidialogut)
            try {
                if (permissions != null) {

                    if (permissions.contentEquals(arrayOf("No permissions Found"))) {
                        texti.text = "No permissions found"
                    } else {
                        for (i in 0 until permissions.size) {
                            texti.append(permissions.get(i).substring(19) + "\n")
                        }
                    }
                } else {
                    texti.text = "No permissions found"
                }
            } catch (e: Exception) {
            }
            dialog.setCancelable(true)
            dialog.show()
        }

        appdetailsbtn.setOnClickListener {
            val intent = Intent().apply {
                setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                addCategory(Intent.CATEGORY_DEFAULT)
                setData(Uri.parse("package:" + apppkg))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
            startActivity(intent)
        }

        extracttext.setOnClickListener {
            if (Tools.checkPermission(this)) {
                extracttext.showProgress {
                    buttonTextRes = R.string.exctracting
                    progressColor = Color.WHITE
                }
                Tools.extractApk(whichapp)
                val rootview: View = window.decorView.findViewById(android.R.id.content)
                extracttext.hideProgress(R.string.extracted)
                Snackbar.make(rootview, "Apk Extracted", Snackbar.LENGTH_SHORT).show()
            }
        }

        sharebtn.setOnClickListener {
            if (Tools.checkPermission(this)) {
                val intent = Tools.getShareableIntent(whichapp, this)
                startActivity(Intent.createChooser(intent, "Share Via"))
            }
        }

        launchbtn.setOnClickListener {
            try {
                startActivity(packageManager.getLaunchIntentForPackage(apppkg!!))
            } catch (e: Exception) {
                Snackbar.make(find(android.R.id.content), "Cannot open this app", Snackbar.LENGTH_SHORT).show()
            }
        }

        //checking to not uninstall this app and also to not launch this app again
        if (apppkg == "com.mateomalaj.appkapkextractor") {
            uninstallbtn.visibility = View.GONE
            launchbtn.visibility = View.GONE
        }

        // checking if a package is deleted
        apackage = apppkg!!

        // setting app info stuff
        icon_iv.setImageDrawable(appig)
        name_tv.text = appname
        pkg_tv.text = apppkg
        install_tv.text = getdate(dateinstalled!!)
        lupdated_tv.text = getlastinstall(lastupdated!!)
        apksize_tv.text = apksize
    }

    // animations function
    fun animations(icon: View, duration: Long) {
        val renderi = Render(this)
        renderi.setAnimation(Zoom().In(icon))
        renderi.setDuration(duration)
        renderi.start()
    }

    // install date converter to string
    fun getlastinstall(date: Long): String {
        var lastupdate = ""
        try {
            var lastupdateString = changedate(date, "MM/dd/yyyy hh:mm:ss")
            lastupdate = lastupdateString
        } catch (e: PackageManager.NameNotFoundException) {
            val installdate = Date(0)
            lastupdate = installdate.toString()
        }
        return lastupdate
    }

    // same
    fun getdate(date: Long): String {
        var installdateformatted = ""
        try {
           val installdatestring = changedate(date, "MM/dd/yyyy hh:mm:ss")
            installdateformatted = installdatestring
        } catch (e: PackageManager.NameNotFoundException) {
            val installdate = Date(0)
            installdateformatted = installdate.toString()
        }
        return installdateformatted
    }

    // long to string for date
    fun changedate(milliseconds: Long, format: String): String {
        val formatter = SimpleDateFormat(format)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }

    // kerkoj qe nese nje app eshte fshire ben exception dhe kthen true
    fun ifdeleted(): Boolean {
        return try {
            packageManager.getPackageInfo(apackage,0)
            false
        } catch (e: PackageManager.NameNotFoundException) {
            true
        }
    }

    // go back with animation gjithashtu nqs eshte fshire i ben reload appit se notifydatasetchanged is nowhere to be found..E dua android studion <3
    override fun onBackPressed() {
        val transition = Transitions(this)
        transition.setAnimation(Slide().InDown())
        if (ifdeleted() == true) {
            apklistSystem.clear()
            apklistGoogle.clear()
            apklistInstalled.clear()
            val intent = Intent(this,MainActivity()::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            super.onBackPressed()
            transition.setAnimation(Slide().InDown())
        }
    }
}