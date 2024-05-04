package com.example.hooghirmohammedfinalproject

/* Took from FirebaseAuthenticationDemo */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.BuildConfig
import com.firebase.ui.auth.IdpResponse
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Log.d(TAG, "onCreate: RegisterActivity")

        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser


        // If currentUser is not null, we have a user and go back to the MainActivity
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity
        } else {
            Log.d(TAG, "Initializing the ActivityResultLauncher for sign-in.")
            // create a new ActivityResultLauncher to launch the sign-in activity and handle the result
            val signActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d(TAG, "ActivityResultLauncher: Received result with resultCode = ${result.resultCode}")
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Sign-in was successful")
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d(TAG, "onActivityResult: Current user = $user")

                    // Checking for User (New/Old)
                    if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp) {
                        Log.d(TAG, "This is a new user")
                        Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d(TAG, "This is a returning user")
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                    }

                    startActivity(Intent(this, MainActivity::class.java))
                    Log.d(TAG, "Navigating to MainActivity")
                    finish()
                    Log.d(TAG, "RegisterActivity finished")
                } else {
                    Log.d(TAG, "Sign-in failed with resultCode = ${result.resultCode}")
                    val response = IdpResponse.fromResultIntent(result.data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: the user has cancelled the sign in request")
                    } else {
                        Log.e(TAG, "onActivityResult: ${response.error?.errorCode}")
                    }
                }
            }

            // Login Button
            findViewById<Button>(R.id.login_button).setOnClickListener {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                    .setAlwaysShowSignInMethodScreen(true)
                    .setIsSmartLockEnabled(false)
                    .build()

                signActivityLauncher.launch(signInIntent)
            }
        }
    }
}