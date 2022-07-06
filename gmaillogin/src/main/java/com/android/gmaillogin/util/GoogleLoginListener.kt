package com.android.gmaillogin.util

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface GoogleLoginListener {
    fun onGmailLoginError(error:String)
    fun onGetProfileSuccess(map: HashMap<String, String?>)
}