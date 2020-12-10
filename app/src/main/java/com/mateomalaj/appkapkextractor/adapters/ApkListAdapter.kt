package com.mateomalaj.appkapkextractor.adapters

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.format.Formatter
import android.text.format.Formatter.formatFileSize
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.ApkModel
import com.mateomalaj.appkapkextractor.MainActivity
import com.mateomalaj.appkapkextractor.R
import com.mateomalaj.appkapkextractor.Tools
import org.jetbrains.anko.find
import org.w3c.dom.Text
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ApkListAdapter(var apklist: ArrayList<ApkModel>, val context: Context): RecyclerView.Adapter<ApkListAdapter.ApkListViewHolder>() {

    var mItemClickListener: FunctionsOnMain? = null
    init {
        mItemClickListener = context as MainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApkListViewHolder {
        return ApkListViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false), context, apklist)
    }

    override fun onBindViewHolder(holder: ApkListViewHolder, position: Int) {
        holder.appicon.setImageDrawable(context.packageManager.getApplicationIcon(apklist.get(position).appinfo))
        holder.appname.text = context.packageManager.getApplicationLabel(apklist.get(position).appinfo).toString()
        holder.apppkg.text = apklist.get(position).packagename

        var installdateString = ""
        var lastupdateString = ""

        fun getdate(): String {
            try {
                var installdate = apklist.get(position).installdate!!
                installdateString = changedate(installdate, "MM/dd/yyyy hh:mm:ss")
            } catch (e: PackageManager.NameNotFoundException) {
                val installdate = Date(0)
                installdateString = installdate.toString()
            }
            return installdateString
        }
        holder.installdate.setText(getdate())

        fun getlastinstall(): String {
            try {
                var lastupdate = apklist.get(position).lastupdated!!
                lastupdateString = changedate(lastupdate, "MM/dd/yyyy hh:mm:ss")
            } catch (e: PackageManager.NameNotFoundException) {
                val installdate = Date(0)
                lastupdateString= installdate.toString()
            }
            return lastupdateString
        }
        holder.lastupdated.setText(getlastinstall())

        val apksizeinfo = context.packageManager.getApplicationInfo(apklist.get(position).packagename!!, 0)
        val apksizeLong = File(apksizeinfo.publicSourceDir).length()
        val apksizeString = Formatter.formatFileSize(context,apksizeLong)
        holder.apksize.setText(apksizeString)
    }

    override fun getItemCount(): Int {
        return apklist.size
    }





    inner class ApkListViewHolder(view: View,context: Context,apkList: ArrayList<ApkModel>): RecyclerView.ViewHolder(view) {
        val appicon: ImageView = view.find(R.id.appicon_iv)
        val appname: TextView = view.find(R.id.appname_tv)
        val apppkg: TextView = view.find(R.id.apppkg_tv)
        val dropdownbtn: ImageView = view.find(R.id.dropdown_iv)
        val extractbtn: TextView = view.find(R.id.extract_tv)
        val sharebtn: TextView = view.find(R.id.share_tv)
        val uninstallbtn: TextView = view.find(R.id.share_tv)
        val installdate: TextView = view.find(R.id.installdate_tv)
        val lastupdated: TextView = view.find(R.id.lastupdated_tv)
        val permissionsbtn: Button = view.find(R.id.permissions_btn)
        val appdetailsbtn: Button = view.find(R.id.details_btn)
        val launchbtn: TextView = view.find(R.id.launch_tv)
        val openplaybtn: TextView = view.find(R.id.openplay_tv)
        val cardview: CardView = view.find(R.id.cardview2)
        val apksize: TextView = view.find(R.id.apksize)

        init {
            extractbtn.setOnClickListener{
                if (Tools.checkPermission(context as MainActivity)) {
                    Tools.extractApk(apkList.get(adapterPosition))
                    val rootview: View = context.window.decorView.findViewById(android.R.id.content)
                    Snackbar.make(rootview, "Apk Extracted Succesfully", Snackbar.LENGTH_SHORT).show()
                }
            }
            sharebtn.setOnClickListener {
                if (Tools.checkPermission(context as MainActivity)) {
                    val intent = Tools.getShareableIntent(apklist.get(adapterPosition), context)
                    context.startActivity(Intent.createChooser(intent, "Share Via"))
                }
            }
            uninstallbtn.setOnClickListener{
                val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
                uninstallIntent.data = Uri.parse("package:" + apkList[adapterPosition].packagename)
                uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
                context.startActivity(uninstallIntent)
            }
            dropdownbtn.setOnClickListener {
                cardview.visibility = View.VISIBLE
                notifyDataSetChanged()
            }
            appdetailsbtn.setOnClickListener{
                val intent = Intent().apply {
                    setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    addCategory(Intent.CATEGORY_DEFAULT)
                    setData(Uri.parse("package:" + apkList.get(adapterPosition).packagename))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }
                context.startActivity(intent)
            }
            launchbtn.setOnClickListener {
                mItemClickListener?.launchandplay(R.id.launch_tv, apkList.get(adapterPosition).packagename!!)
            }
            openplaybtn.setOnClickListener {
                mItemClickListener?.launchandplay(R.id.openplay_tv, apkList.get(adapterPosition).packagename!!)
            }
        }
    }

    fun changedate(milliseconds: Long, format: String): String {
        val formatter= SimpleDateFormat(format)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }

    interface FunctionsOnMain {
        fun launchandplay(thing: Int, who: String)
        fun uninstallApp(uri: Uri)
        fun getpermissions(kush: String) : Array<String>
    }
}