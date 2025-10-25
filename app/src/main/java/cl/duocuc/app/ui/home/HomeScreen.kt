package cl.duocuc.app.ui.home


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duocuc.app.R
import cl.duocuc.app.ui.home.components.AnimatedLogo
import androidx.compose.ui.graphics.Color
import cl.duocuc.app.ui.theme.VerdeEsmeralda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRecoverClick: () -> Unit,
) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Huerto Hogar App") }) },
            containerColor = Color.Transparent
        ) { inner ->
            HomeContent(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp),
                onLoginClick = onLoginClick,
                onRegisterClick = onRegisterClick,
                onRecoverClick = onRecoverClick
            )
        }

}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRecoverClick: () -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedLogo(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
        Text("¡Bienvenido!",style=MaterialTheme.typography.headlineMedium)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onLoginClick) { Text("Login") }
            Button(onClick = onRegisterClick , colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Text("Registrarse",color = VerdeEsmeralda) }
            Button(onClick = onRecoverClick , colors = ButtonDefaults.buttonColors(containerColor = Color.White)) { Text("Recuperar contraseña",color = VerdeEsmeralda) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onLoginClick = {},
        onRegisterClick = {},
        onRecoverClick = {}
    )
}
