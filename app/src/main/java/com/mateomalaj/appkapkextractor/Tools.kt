package com.mateomalaj.appkapkextractor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import org.apache.commons.io.FileUtils
import java.io.File

class Tools {
    companion object {
        val STORAGE_PERMISSION_CODE = 1008

        fun checkPermission(activity: AppCompatActivity): Boolean {
            var permissionGranted = false
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    val rootview: View =
                        (activity as MainActivity).window.decorView.findViewById(android.R.id.content)
                    Snackbar.make(rootview, "Storage permission is required", Snackbar.LENGTH_LONG)
                        .setAction("Allow") {
                            ActivityCompat.requestPermissions(
                                activity,
                                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                STORAGE_PERMISSION_CODE
                            )
                        }
                        .setActionTextColor(Color.WHITE)
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
            } else {
                permissionGranted = true
            }
            return permissionGranted
        }

        fun checkStorage(): Boolean {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
        }

        fun getappfolder(): File? {
            var file: File? = null
            if (checkStorage()) {
                file = File(Environment.getExternalStorageDirectory(), "APPK")
                return file
            }
            return file
        }

        fun makeAppDirectory() {
            val file = getappfolder()
            if (file != null && file.exists()) {
                file.mkdir()
            }
        }

        fun extractApk(apk: ApkModel): Boolean {
            makeAppDirectory()
            var extracted = true
            val originalfile = File(apk.appinfo.sourceDir)
            val extractedfile = getApkFile(apk)

            try {
                FileUtils.copyFile(originalfile, extractedfile)
                extracted = true
            } catch (e: Exception) {
                Log.d("test", "problem - " + e.message)
            }
            return extracted
        }

        private fun getApkFile(apk: ApkModel): File {
            var fileName =
                getappfolder()?.path + File.separator + apk.appname + "_" + apk.version + ".apk"
            return File(fileName)
        }

        fun getShareableIntent(apk: ApkModel, context: Context): Intent {
            extractApk(apk)
            var file = getApkFile(apk)
            var shareIntent = Intent().apply {
                setAction(Intent.ACTION_SEND)
                putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(context,context.applicationContext.packageName + ".provider",file))
                type = "application/vnd.android.package-archive"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            return shareIntent
        }

    }
}