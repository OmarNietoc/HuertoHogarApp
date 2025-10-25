package cl.duocuc.app.ui.principal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duocuc.app.ui.profile.ProfileScreen
import cl.duocuc.app.ui.profile.ProfileViewModel
import cl.duocuc.app.repository.auth.FirebaseAuthDataSource
import cl.duocuc.app.data.media.MediaRepository
import cl.duocuc.app.ui.principal.components.UiProductosCard
import cl.duocuc.app.ui.recordatorio.RecordatorioScreen
import cl.duocuc.app.ui.recordatorio.RecordatorioViewModel
import cl.duocuc.app.ui.vmfactory.RecordatorioVMFactory
import cl.duocuc.app.ui.vmfactory.ProfileVMFactory
import com.google.firebase.auth.FirebaseAuth
import cl.duocuc.app.data.repository.ProductoRepository
import cl.duocuc.app.data.local.AppDatabase
import kotlinx.coroutines.launch
// --- Bottom items ---
sealed class BottomItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badge: Int? = null
) {
    data object Home : BottomItem("home", "Inicio", Icons.Outlined.Home)
    data object Favs : BottomItem("favs", "Favoritos", Icons.Outlined.FavoriteBorder)
    data object Cart : BottomItem("cart", "Carrito", Icons.Outlined.ShoppingCart, badge = 3)

    data object Agenda : BottomItem("agenda", "Agenda", Icons.Outlined.PlayArrow)
    data object More : BottomItem("more", "Más", Icons.Outlined.Menu)
}

private val bottomItems = listOf(
    BottomItem.Home, BottomItem.Favs, BottomItem.Cart, BottomItem.Agenda, BottomItem.More
)

@Composable
private fun BottomBar(
    navController: NavHostController,
    onHomeTap: () -> Unit
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == BottomItem.Home.route) {
                        // SIEMPRE refrescamos Home y NO restauramos estado
                        onHomeTap()
                        navController.navigate(BottomItem.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    } else {
                        // Resto de tabs con preservación de estado
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if ((item.badge ?: 0) > 0) {
                        BadgedBox(badge = { Badge { Text("${item.badge}") } }) {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrincipalScreen(
    email: String,
    onLogout: () -> Unit = {},
    vm: PrincipalViewModel = viewModel()

) {
    val state by vm.ui.collectAsState()
    val categoriaSel by vm.categoriaSel.collectAsState()
    val productos by vm.productosFiltrados.collectAsState()


    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val productoRepository = remember { ProductoRepository(db) }
    val scope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }
    val tabsNav = rememberNavController()

    // Logout reactivo
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    // Snackbar para errores
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Principal") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                expanded = false
                                tabsNav.navigate("profile")
                            },
                            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = { expanded = false },
                            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                vm.logout()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(tabsNav, onHomeTap = { vm.refreshHome() }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent,
    ) { inner ->
        NavHost(
            navController = tabsNav,
            startDestination = BottomItem.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            // HOME
            composable(route = BottomItem.Home.route) {
                // Carga inicial en el primer ingreso
                LaunchedEffect(Unit) {
                    if (productos.isEmpty()) vm.cargarProductos()
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val saludo = "Hola ${email}"
                    Text(saludo, style = MaterialTheme.typography.headlineSmall)
                    Text("Bienvenido a tu pantalla principal.")

                    // Filtros por categoría
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(vm.categorias.size) { idx ->
                            val cat = vm.categorias[idx]
                            FilterChip(
                                selected = categoriaSel == cat,
                                onClick = { vm.setCategoria(cat) },
                                label = { Text(cat) }
                            )
                        }
                    }
                    // Grilla de productos
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                    ) {
                        items(productos, key = { it.id }) { producto ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.6f)
                                    .animateContentSize()
                            ) {
                                UiProductosCard(
                                    producto = producto,
                                    onAgregar = { /* Navegar al detalle */ },
                                    onToggleFavorito = {
                                        // 🔹 Aquí creamos el nuevo producto con favorito toggled
                                        val nuevoProducto = producto.copy(favorito = !producto.favorito)

                                        // 🔹 Ejecutamos repo + ViewModel en coroutine
                                        scope.launch {
                                            productoRepository.toggleFavorito(nuevoProducto)
                                            vm.actualizarProductoFavorito(nuevoProducto)
                                        }
                                    }
                                )
                            }
                        }
                    }

                }
            }

            // FAVORITOS
            composable(BottomItem.Favs.route) {

                val todos by vm.todosProductos.collectAsState()
                val soloFavoritos = todos.filter { it.favorito }


                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(soloFavoritos, key = { it.id }) { producto ->
                        UiProductosCard(
                            producto = producto,
                            onAgregar = { /* carrito */ },
                            onToggleFavorito = { vm.actualizarProductoFavorito(producto.copy(favorito = !producto.favorito)) }
                        )
                    }
                }
            }



            // CARRITO
            composable(BottomItem.Cart.route) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Carrito")
                }
            }

            // AGENDA
            composable(BottomItem.Agenda.route) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Debes iniciar sesión para ver tus recordatorios.")
                    }
                } else {
                    val context = LocalContext.current
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@composable

                    val factory = remember(uid) { RecordatorioVMFactory(context, uid) }
                    val rvm: RecordatorioViewModel = viewModel(factory = factory)
                    RecordatorioScreen(rvm)
                }
            }

            // MÁS
            composable(BottomItem.More.route) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text("Más opciones")
                    Button(onClick = { vm.logout() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.loading) "Cerrando..." else "Cerrar sesión")
                    }
                }
            }

            // PERFIL
            composable("profile") {

                val authDs = remember { FirebaseAuthDataSource() }
                val mediaRepo = remember { MediaRepository() }
                val factory = remember { ProfileVMFactory(authDs, mediaRepo) }
                val pvm: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(pvm)
                ProfileScreen(pvm)
            }
        }
    }
}
