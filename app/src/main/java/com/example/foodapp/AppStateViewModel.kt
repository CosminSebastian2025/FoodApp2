package com.example.foodapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.foodapp.data.SessionStore

/** Conserva lo stato dell'app (carrello, navigazione, utente) durante le rotazioni. */
class AppStateViewModel(application: Application) : AndroidViewModel(application) {
    val state = AppState(SessionStore(application))
}
