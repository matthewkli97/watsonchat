package edu.piedpiper.uw.ischool.watsonchat
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.Intent

import android.database.Cursor
import com.firebase.ui.auth.AuthUI
import java.util.*
import java.util.Arrays.asList
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth


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
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) { // Signed in user
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        setContentView(R.layout.activity_login)

        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN)
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

}
