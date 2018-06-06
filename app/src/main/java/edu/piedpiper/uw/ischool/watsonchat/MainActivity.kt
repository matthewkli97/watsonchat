package edu.piedpiper.uw.ischool.watsonchat
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent

import android.database.Cursor
import android.net.ConnectivityManager
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

        airplaneMode()
        checkConnection()

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

        airplaneMode()
        checkConnection()
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    fun airplaneMode() {
        if (Settings.Global.getInt(this.contentResolver,
                        Settings.Global.AIRPLANE_MODE_ON, 0) !== 0) {
            val dialog = AlertDialog.Builder(this)

            dialog.setTitle("Airplane Mode")
            dialog.setMessage("You currently have airplane mode on so features such as chat" +
                    " and Watson personality analysis might not work as expected. Do you want to head over to settings" +
                    " to turn it off?")

            dialog.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                startActivity(intent)

            }

            dialog.setNegativeButton("No") { _, _ ->

            }

            val d: AlertDialog = dialog.create()

            d.show()
        }
    }

    private fun checkConnection() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            val dialog = AlertDialog.Builder(this)

            dialog.setTitle("No Connection")

            dialog.setMessage("You currently have no internet connection so features such as chat" +
                    " and Watson personality analysis will not work as expected. Do you want to connect" +
                    " to a wifi network?")

            dialog.setPositiveButton("Yes") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)

            }

            dialog.setNegativeButton("No") { _, _ ->

            }


            val d: AlertDialog = dialog.create()

            d.show()
        }
    }


}
