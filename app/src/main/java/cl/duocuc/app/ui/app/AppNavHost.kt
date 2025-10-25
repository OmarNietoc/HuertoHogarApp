package cl.duocuc.app.ui.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duocuc.app.R
import cl.duocuc.app.ui.home.HomeScreen
import cl.duocuc.app.ui.login.LoginScreen
import cl.duocuc.app.ui.principal.PrincipalScreen
import cl.duocuc.app.ui.register.RegistrarseScreen
import cl.duocuc.app.ui.recover.RecuperarPasswordScreen
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.navigation.NavType
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    BackgroundImage {

        // NavHost con tus composables encima del fondo
        NavHost(navController = nav, startDestination = Route.HomeRoot.path) {

            composable(Route.HomeRoot.path) {
                    HomeScreen(
                        onLoginClick = { nav.navigate(Route.Login.path) },
                        onRegisterClick = { nav.navigate(Route.Register.path) },
                        onRecoverClick = { nav.navigate(Route.RecoverPassword.path) }
                    )
            }
            composable(Route.Login.path) {
                LoginScreen(
                    onBack = { nav.popBackStack() },
                    onLoginSuccess = { email ->
                        nav.navigate("${Route.Principal.path}/$email") {
                            launchSingleTop = true
                            popUpTo(Route.HomeRoot.path) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = "${Route.Principal.path}/{email}",
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""

                PrincipalScreen(
                    email = email,
                    onLogout = {
                        nav.navigate(Route.HomeRoot.path) {
                            popUpTo(Route.HomeRoot.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Route.Register.path) {
                RegistrarseScreen(
                    onBack = { nav.popBackStack() },
                    onRegistered = {
                        nav.navigate(Route.Login.path) {
                            popUpTo(Route.HomeRoot.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Route.RecoverPassword.path) {
                RecuperarPasswordScreen(
                    onBack = { nav.popBackStack() },
                    onSent = {
                        nav.navigate(Route.Login.path) {
                            popUpTo(Route.HomeRoot.path) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
   }
}
