package edu.piedpiper.uw.ischool.watsonchat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class ConnectivityChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        debugIntent(intent, "grokkingandroid")
    }

    private fun debugIntent(intent: Intent, tag: String) {
        Log.v(tag, "action: " + intent.action!!)
        Log.v(tag, "component: " + intent.component!!)
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                Log.v(tag, "key [" + key + "]: " +
                        extras.get(key))
            }
        } else {
            Log.v(tag, "no extras")
        }
    }

}