package com.amr_medhat_r.dentale_commerceapp.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthClass {

    private val mFirebaseAuth = FirebaseAuth.getInstance()

    fun sendEmailToResetPassword(email: String): Task<Void> {
        // Reset password using FirebaseAuth
        return mFirebaseAuth.sendPasswordResetEmail(email)
    }

    fun logInRegisteredUser(email: String, password: String): Task<AuthResult> {
        // Log-In using FirebaseAuth
        return mFirebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun registerUser(email: String, password: String): Task<AuthResult> {
        // Create an instance and create a register a user with email and password.
        return mFirebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUserID(): String {
        // An Instance of current user using FirebaseAuth
        val currentUser = mFirebaseAuth.currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentID = ""
        if (currentUser != null) {
            currentID = currentUser.uid
        }

        return currentID
    }

    fun signOut() {
        mFirebaseAuth.signOut()
    }
}