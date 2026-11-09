package com.example.foodapp

import androidx.lifecycle.ViewModel

/** Conserva lo stato dell'app (carrello, navigazione, utente) durante le rotazioni. */
class AppStateViewModel : ViewModel() {
    val state = AppState()
}
