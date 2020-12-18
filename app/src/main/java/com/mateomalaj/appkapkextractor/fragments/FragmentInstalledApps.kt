package com.mateomalaj.appkapkextractor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mateomalaj.appkapkextractor.PackageAttributes
import com.mateomalaj.appkapkextractor.R
import com.mateomalaj.appkapkextractor.adapters.ApkListAdapter
import com.mateomalaj.appkapkextractor.apklistGoogle
import com.mateomalaj.appkapkextractor.apklistInstalled
import org.jetbrains.anko.find

class FragmentInstalledApps : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_installed_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyler: RecyclerView = view!!.find(R.id.recyclerviewinstalled)
        val mlayout = LinearLayoutManager(context)
        val installedadapter = ApkListAdapter(apklistInstalled, context!!)
        recyler.layoutManager = mlayout
        recyler.adapter = installedadapter
        installedadapter.notifyDataSetChanged()
    }
}