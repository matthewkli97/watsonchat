package edu.piedpiper.uw.ischool.watsonchat
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.*

import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.firebase.ui.auth.AuthUI
import java.util.*
import java.util.Arrays.asList
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.os.Build
import android.provider.Settings


/**
 * A login screen that offers login via email/password.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private val RC_SIGN_IN = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN)
        } else {
            startMessage()
        }
    }

    private fun startMessage() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().uid
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        userRef.setValue(user!!.displayName)
                .addOnSuccessListener(OnSuccessListener<Void> {
                    Log.i("MessageActivity", "Dafux")
                    startActivity(Intent(this, ThreadActivity::class.java))
                    finish()
                })
                .addOnFailureListener(OnFailureListener {
                    Log.i("MessageActivity", "Failure")
                })
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startMessage()
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun onResume() {
        super.onResume()
        if (isAirplaneModeOn(applicationContext)) displayAlert()
        registerReceiver(airplaneReceiver, IntentFilter("AIRPLANE_MODE"))
    }

    var airplaneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            displayAlert()
        }
    }

    private fun displayAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
            startActivityForResult(Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS), 0)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
        builder.setMessage("Airplane mode is on. Would you like to turn off Airplane mode?")
                .setTitle("Connectivity Issues")

        val dialog = builder.create()
        dialog.show()
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        Log.i("Main", "AirplaneModeOn")
        return Settings.Global.getInt(context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    }

    class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            if (isAirplaneModeOn(context)) {
                Toast.makeText(context, "Turn Off Airplane Mode", Toast.LENGTH_SHORT).show()
            }
            if (!isWifiOn(context)) {
                Toast.makeText(context, "Turn Wifi On", Toast.LENGTH_SHORT).show()
            }
        }


        private fun isAirplaneModeOn(context: Context): Boolean {
            return Settings.Global.getInt(context.contentResolver,
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0
        }

        private fun isWifiOn(context: Context): Boolean {
            return Settings.Global.getInt(context.contentResolver,
                    Settings.Global.WIFI_ON, 0) != 0
        }
    }
}
