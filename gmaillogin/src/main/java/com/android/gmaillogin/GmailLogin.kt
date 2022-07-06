package com.android.gmaillogin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.android.gmaillogin.util.GoogleLoginListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GmailLogin {
    companion object {
        private var mGoogleSignInClient: GoogleSignInClient? = null
        private var gso: GoogleSignInOptions? = null
        private var account: GoogleSignInAccount? = null
        private var googleLoginListener: GoogleLoginListener? = null

        fun initGoogleClient(
            context: Context,
            mGoogleLoginListener: GoogleLoginListener,
            clientID: String,
        ) {
            googleLoginListener = mGoogleLoginListener
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientID)
                .requestEmail()
                .build()
            mGoogleSignInClient = gso?.let { GoogleSignIn.getClient(context, it) }
            account = GoogleSignIn.getLastSignedInAccount(context)
            System.err.println(">>>> init google")
        }

        fun signIn(launcher: ActivityResultLauncher<Intent>) {
            mGoogleSignInClient?.signOut()
            val signInIntent = mGoogleSignInClient?.signInIntent
            if (signInIntent != null) {
                System.err.println(">>>> signin ")
                launcher.launch(signInIntent)
            }
        }

        fun requestForUserdata(data: Intent?) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
            try {
                System.err.println(">>>> handle signin result")
                val account = completedTask.getResult(ApiException::class.java)

                if (account != null) {
                    val map = HashMap<String, String?>()
                    map["id_token"] = account.idToken
                    map["name"] = account.displayName
                    map["email"] = account.email
                    map["gmail_id"] = account.id
                    if (account.photoUrl != null) {
                        map["image"] = account.photoUrl.toString()
                    }
                    googleLoginListener?.onGetProfileSuccess(map)
                }
            } catch (e: ApiException) {
                System.err.println(">>>> error signin result")
                googleLoginListener?.onGmailLoginError(e.localizedMessage)
                Log.w("Tag", "signInResult:failed code=" + e.statusCode)
            }
        }
    }
}
