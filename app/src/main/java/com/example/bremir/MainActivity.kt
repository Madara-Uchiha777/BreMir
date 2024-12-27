package com.example.bremir

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.bremir.datastore.IDataStoreRepository
import com.example.bremir.navigation.Destination
import com.example.bremir.navigation.NavGraph
import com.example.bremir.ui.theme.BreMirTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreRepository: IDataStoreRepository

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val username = dataStoreRepository.getString("EMAIL_KEY")

            // Urči startovací obrazovku na základě přihlášení
            val startDestination = if (username.isNullOrEmpty()) {
                Destination.LoginScreen.route // Pokud není přihlášen
            } else {
                Destination.MainScreen.route // Pokud je přihlášen
            }

            setContent {
                BreMirTheme {
                    NavGraph(
                        startDestination = startDestination,
                        navController = rememberNavController()
                    )
                }
            }
        }
    }
}