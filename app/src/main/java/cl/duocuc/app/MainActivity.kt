package cl.duocuc.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import cl.duocuc.app.ui.app.AppNavHost
import cl.duocuc.app.ui.theme.HuertoHogarTheme
import cl.duocuc.app.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuertoHogarTheme {

                AppNavHost()
            }
        }
    }
}

