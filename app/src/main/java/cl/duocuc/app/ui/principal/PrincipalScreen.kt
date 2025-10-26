package cl.duocuc.app.ui.principal

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import cl.duocuc.app.data.repository.OrderRepository
import cl.duocuc.app.ui.vmfactory.PrincipalVMFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.ui.text.style.TextAlign
import cl.duocuc.app.ui.principal.components.ProductoCardCarrito

// --- Bottom items ---
sealed class BottomItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badge: Int? = null
) {
    data object Home : BottomItem("home", "Inicio", Icons.Outlined.Home)
    data object Favs : BottomItem("favs", "Favoritos", Icons.Outlined.FavoriteBorder)
    data object Cart : BottomItem("cart", "Carrito", Icons.Outlined.ShoppingCart)

    data object Agenda : BottomItem("agenda", "Agenda", Icons.Outlined.PlayArrow)
    data object More : BottomItem("more", "Más", Icons.Outlined.Menu)
}

private val bottomItems = listOf(
    BottomItem.Home, BottomItem.Favs, BottomItem.Cart, BottomItem.Agenda, BottomItem.More
)

@Composable
private fun BottomBar(
    navController: NavHostController,
    onHomeTap: () -> Unit,
    cantidadCarrito: Int
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
                    if (item == BottomItem.Cart && cantidadCarrito > 0) {
                        BadgedBox(
                            badge = { Badge { Text("$cantidadCarrito") } }
                        ) {
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
    onLogout: () -> Unit = {} ,
    //vm: PrincipalViewModel = viewModel()

) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val dao = remember { db.productoFavoritoDao() } // ✅ obtiene el dao
    val factory = remember { PrincipalVMFactory(db, dao) }
    val vm: PrincipalViewModel = viewModel(factory = factory)
    val state by vm.ui.collectAsState()
    val categoriaSel by vm.categoriaSel.collectAsState()
    val productos by vm.productosFiltrados.collectAsState()
    val productoRepository = remember { ProductoRepository(db) }
    var expanded by remember { mutableStateOf(false) }
    val tabsNav = rememberNavController()
    val scope = rememberCoroutineScope()
    val orderRepository = remember { OrderRepository(AppDatabase.get(context)) }
    val cantidadCarrito by vm.cantidadCarrito.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {
            onLogout()
            vm.resetLogoutState() // Reinicia loggedOut = false para la próxima vez
        }
    }

    LaunchedEffect(email) {
        vm.inicializarProductos()
        vm.cargarCarrito()
    }

    // Snackbar para alertas
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 90.dp)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            }
        },
        containerColor = Color.Transparent
    ) { padding ->

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
            bottomBar = { BottomBar(
                tabsNav,
                onHomeTap = { vm.refreshHome() },
                cantidadCarrito=cantidadCarrito) },
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
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = MaterialTheme.colorScheme.onSurface,
                                    )
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
                                        onAgregar = { p-> vm.agregarAlCarrito(p)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Se agregó el producto ${p.nombre} al carrito!"
                                                )
                                            }},
                                        onToggleFavorito = {
                                            // 🔹 Aquí creamos el nuevo producto con favorito toggled
                                            val nuevoProducto = producto.copy(favorito = !producto.favorito)

                                            // repo + ViewModel en coroutine
                                            scope.launch {

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

                    if (soloFavoritos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f) // Ocupa el 80% del ancho para mejor apariencia
                                    .padding(16.dp), // Padding externo
                                shadowElevation = 4.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp), // Padding interno para el texto
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Agrega productos a tu lista de favoritos!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    else{
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 180.dp),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(soloFavoritos, key = { it.id }) { producto ->
                                UiProductosCard(
                                    producto = producto,
                                    onAgregar = { p-> vm.agregarAlCarrito(p)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Se agregó el producto ${p.nombre} al carrito!"
                                            )
                                        }},
                                    onToggleFavorito = { vm.actualizarProductoFavorito(producto.copy(favorito = !producto.favorito)) }
                                )
                            }
                        }
                    }

                }



                // CARRITO
                composable(BottomItem.Cart.route) {
                    val carrito by vm.carrito.collectAsState()
                    val total = vm.totalCarrito()
                    LaunchedEffect(Unit) {
                        vm.cargarCarrito()
                    }

                    if (carrito.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                color = Color.White,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f) // Ocupa el 80% del ancho para mejor apariencia
                                    .padding(16.dp), // Padding externo
                                shadowElevation = 4.dp
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp), // Padding interno para el texto
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Tu carrito está vacío 🛒",
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center // Centra el texto horizontalmente
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text("Carrito de compras", style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.height(8.dp))

                                // Lista de productos con espacio para el total fijo
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(bottom = 120.dp) // Espacio para el total fijo
                                ) {
                                    items(carrito) { item ->
                                        Surface(
                                            color = Color.White,
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier.fillMaxWidth(),
                                            shadowElevation = 4.dp
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Card del producto con selector de cantidad
                                                Box(modifier = Modifier.weight(1f)) {
                                                    ProductoCardCarrito(
                                                        producto = item.producto,
                                                        cantidad = item.cantidad,
                                                        onCantidadChange = { nuevaCantidad ->
                                                            if (nuevaCantidad > 0) {
                                                                vm.actualizarCantidadCarrito(item.producto.id, nuevaCantidad)
                                                            } else {
                                                                vm.eliminarDelCarrito(item.producto.id)
                                                            }
                                                        }
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                // Ícono de eliminar
                                                IconButton(
                                                    onClick = {
                                                        vm.eliminarDelCarrito(item.producto.id)
                                                    },
                                                    modifier = Modifier.size(48.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = "Eliminar del carrito",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // TOTAL FIJO EN LA PARTE INFERIOR
                            Surface(
                                color = Color.White,
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Total:",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            "$$total",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            vm.limpiarCarrito()
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "Compra realizada, va en camino del Huerto a tu Hogar!"
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            "Completar compra",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
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
}
