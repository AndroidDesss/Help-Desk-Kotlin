package com.androidx.helpdesk

import android.app.AlertDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.os.Build
import android.app.Activity
import android.net.ConnectivityManager

class CommonMethod(private val context: Context) {
    interface DialogClickListener {
        fun dialogOkBtnClicked(value: String?)
        fun dialogNoBtnClicked(value: String?)
    }

    companion object {

        fun getCurrentDate(mContext: Context?): String {
            val curFormat = SimpleDateFormat("MM/dd/yyyy")
            val dateObj = Date()
            return curFormat.format(dateObj)
        }

        fun showToast(mContext: Context?, message: String?) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }

        fun showAlertDialog(context: Context?, title: String?, message: String?, positiveBtnText: String?, negativeBtnText: String?, dialogClickListener: DialogClickListener)
        {
            val alertDialogBuilder = AlertDialog.Builder(context)
            if (title != null && title.length > 0) alertDialogBuilder.setTitle(title)
            if (message != null && message.length > 0) alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setPositiveButton(positiveBtnText, null)
            if (negativeBtnText != null) {
                alertDialogBuilder.setNegativeButton(negativeBtnText, null)
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.setOnShowListener {
                val okBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                okBtn.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    okBtn.setTextAppearance(R.style.StyleAlertBox)
                } else {
                    okBtn.setTextAppearance(context, R.style.StyleAlertBox)
                }
                okBtn.setOnClickListener {
                    dialogClickListener.dialogOkBtnClicked("")
                    alertDialog.dismiss()
                }
                val cancelBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                cancelBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                cancelBtn.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cancelBtn.setTextAppearance(R.style.StyleAlertBox)
                } else {
                    cancelBtn.setTextAppearance(context, R.style.StyleAlertBox)
                }
                cancelBtn.setOnClickListener {
                    dialogClickListener.dialogNoBtnClicked("")
                    alertDialog.dismiss()
                }
            }
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.setCancelable(false)
            alertDialog.show()
        }



        fun isNetworkAvailable(act: Activity): Boolean {
            val connectivityManager = act.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}