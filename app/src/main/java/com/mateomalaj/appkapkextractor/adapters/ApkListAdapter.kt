package com.mateomalaj.appkapkextractor.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mateomalaj.appkapkextractor.ApkModel
import com.mateomalaj.appkapkextractor.MainActivity
import com.mateomalaj.appkapkextractor.PackageAttributes
import com.mateomalaj.appkapkextractor.R
import layout.transitions.library.Slide
import layout.transitions.library.Transitions
import org.jetbrains.anko.find
import render.animations.Attention
import render.animations.Render
import java.io.File
import java.util.*

var appig : Drawable? = null
lateinit var whichapp: ApkModel

class ApkListAdapter(var apklist: ArrayList<ApkModel>, val context: Context) : RecyclerView.Adapter<ApkListAdapter.ApkListViewHolder>() {

    // initializing interface
    var mItemClickListener: FunctionsOnMain? = null
    init {
        mItemClickListener = context as MainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApkListViewHolder {
        return ApkListViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false), context, apklist)
    }

    override fun onBindViewHolder(holder: ApkListViewHolder, position: Int) {
        holder.appicon.setImageDrawable(context.packageManager.getApplicationIcon(apklist[position].appinfo))
        holder.appname.text = context.packageManager.getApplicationLabel(apklist[position].appinfo).toString()
    }

    override fun getItemCount(): Int {
        return apklist.size
    }


    inner class ApkListViewHolder(view: View, context: Context, apkList: ArrayList<ApkModel>) : RecyclerView.ViewHolder(view) {
        val appicon: ImageView = view.find(R.id.appicon_iv)
        val appname: TextView = view.find(R.id.appname_tv)
        private val cardview: CardView = view.find(R.id.cardview)


        // initializing setonclickun e cardviews qe te hap package attributin
        // apk modelin e ke lart te variabla whichapp sepse nuk mund ta kalosh me putextra -_-
        init {
            cardview.setOnClickListener {
                try {
                    val intent = Intent(context,PackageAttributes()::class.java)
                    whichapp = apkList[adapterPosition]
                    val appicon = context.packageManager.getApplicationIcon(apklist[adapterPosition].appinfo)
                    appig = appicon
                    val appname = context.packageManager.getApplicationLabel(apklist[adapterPosition].appinfo).toString()
                    val apppkg = apklist[adapterPosition].packagename
                    val dateinstalled = apklist[adapterPosition].installdate!!
                    val lastupdated = apklist[adapterPosition].lastupdated!!
                    val apksize = context.packageManager.getApplicationInfo(apklist[adapterPosition].packagename!!, 0)
                    val apksizeLong = File(apksize.publicSourceDir).length()
                    val apksizeString = Formatter.formatFileSize(context, apksizeLong)
                    val permissions: Array<String>? = mItemClickListener?.getpermissions(apklist[adapterPosition].packagename!!)
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
                } catch (e: Exception) {
                    val rootview: View =
                            (MainActivity()).window.decorView.findViewById(android.R.id.content)
                    Snackbar.make(rootview, "Cannot open app", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
    // interface of MainActit
    interface FunctionsOnMain {
        fun getpermissions(kush: String): Array<String>
    }
}