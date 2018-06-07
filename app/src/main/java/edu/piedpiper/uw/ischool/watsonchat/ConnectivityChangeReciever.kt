package edu.piedpiper.uw.ischool.watsonchat

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log

class ConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("BroadCast", isOnline(context).toString())
        if(!isOnline(context)) {
            displayAlert(context)
        } 
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }

    fun displayAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
            context.startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
        builder.setMessage("You are not connected to the Internet. Airplane mode may be on, your wifi could be off" +
                " or you may not have service. Would you like to go to settings now to try to fix this?")
                .setTitle("Connectivity Issues")

        val dialog = builder.create()
        dialog.show()
    }
}