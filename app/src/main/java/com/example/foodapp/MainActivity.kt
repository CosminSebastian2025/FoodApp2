package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.foodapp.navigation.AppNavigation
import com.example.foodapp.ui.theme.FastBiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FastBiteTheme{
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(context = this@MainActivity)
                }
            }
        }
    }
}