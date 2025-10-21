package com.example.techport


import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestoreSettings

class TechPortApp : Application() {
    override fun onCreate() {
        super.onCreate()

        //  Enable Firestore Offline Caching
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }
}
