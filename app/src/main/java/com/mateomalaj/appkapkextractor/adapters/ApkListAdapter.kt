package com.mateomalaj.appkapkextractor.adapters

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.text.format.Formatter
import android.transition.Transition
import android.transition.TransitionSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.*
import layout.transitions.library.Slide
import layout.transitions.library.Transitions
import org.jetbrains.anko.find
import render.animations.Attention
import render.animations.Render
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
var appig : Drawable? = null
lateinit var whichapp: ApkModel
class ApkListAdapter(var apklist: ArrayList<ApkModel>, val context: Context) : RecyclerView.Adapter<ApkListAdapter.ApkListViewHolder>() {

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
    }

    override fun getItemCount(): Int {
        return apklist.size
    }


    inner class ApkListViewHolder(view: View, context: Context, apkList: ArrayList<ApkModel>) : RecyclerView.ViewHolder(view) {
        val appicon: ImageView = view.find(R.id.appicon_iv)
        val appname: TextView = view.find(R.id.appname_tv)
        val cardview: CardView = view.find(R.id.cardview)

        init {
            cardview.setOnClickListener {
                val intent = Intent(context,PackageAttributes()::class.java)
                whichapp = apkList.get(adapterPosition)
                val appicon = context.packageManager.getApplicationIcon(apklist.get(adapterPosition).appinfo)
                appig = appicon
                val appname = context.packageManager.getApplicationLabel(apklist.get(adapterPosition).appinfo).toString()
                val apppkg = apklist.get(adapterPosition).packagename
                val dateinstalled = apklist.get(adapterPosition).installdate!!
                val lastupdated = apklist.get(position).lastupdated!!
                val apksize = context.packageManager.getApplicationInfo(apklist.get(adapterPosition).packagename!!, 0)
                val apksizeLong = File(apksize.publicSourceDir).length()
                val apksizeString = Formatter.formatFileSize(context, apksizeLong)
                var permissions: Array<String>?
                permissions = mItemClickListener?.getpermissions(apklist.get(adapterPosition).packagename!!)
                intent.apply {
                    putExtra("appname",appname)
                    putExtra("apppkg",apppkg)
                    putExtra("dateinstalled",dateinstalled)
                    putExtra("lastupdated",lastupdated)
                    putExtra("apksize",apksizeString)
                    putExtra("permissions",permissions)
                }
                context.startActivity(intent)
                val transitions = Transitions(context)
                transitions.setAnimation(Slide().InUp())
                val render = Render(context)
                render.setAnimation(Attention().Pulse(cardview))
                render.start()
            }
        }
    }

    interface FunctionsOnMain {
        fun getpermissions(kush: String): Array<String>
    }
}