package cl.duocuc.app.ui.principal


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoEntity
import cl.duocuc.app.data.repository.OrderRepository
import cl.duocuc.app.data.repository.ProductoRepository
import cl.duocuc.app.model.Producto
import cl.duocuc.app.repository.auth.AuthRepository
import cl.duocuc.app.repository.auth.FavoritosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false,

)

class PrincipalViewModel (
    private val orderRepository: OrderRepository,
    private val productoRepository: ProductoRepository,
    private val authRepository: AuthRepository = AuthRepository(),
    private val favoritosRepository: FavoritosRepository,
): ViewModel() {

    // ---------- Fuente mutable ----------
    private val _fuenteMutable = MutableStateFlow<List<Producto>>(emptyList())
    private val fuenteFlow: StateFlow<List<Producto>> = _fuenteMutable.asStateFlow()
    val todosProductos: StateFlow<List<Producto>> = _fuenteMutable.asStateFlow()

    // ---------- Categorías ----------
    private val _categorias = MutableStateFlow<List<String>>(listOf("Todos"))
    val categorias: StateFlow<List<String>> = _categorias.asStateFlow()

    // ---------- Estado general ----------
    private val _ui = MutableStateFlow(PrincipalUiState())
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    private val _categoriaSel = MutableStateFlow("Todos")
    val categoriaSel: StateFlow<String> = _categoriaSel.asStateFlow()

    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    // ---------- Carrito ----------
    data class CartItem(
        val producto: Producto,
        val cantidad: Int
    ) {
        val subtotal: Int get() = producto.precio * cantidad
    }

    private val _carrito = MutableStateFlow<List<CartItem>>(emptyList())
    val carrito: StateFlow<List<CartItem>> = _carrito.asStateFlow()
    private val _cantidadCarrito = MutableStateFlow(0)
    val cantidadCarrito: StateFlow<Int> = _cantidadCarrito

    init {
        actualizarContadorCarrito()
        cargarFavoritos()
        inicializarProductos()
    }

    // ---------- Favoritos -----------------
    private val _favoritosIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritosIds: StateFlow<Set<String>> = _favoritosIds.asStateFlow()

    fun inicializarProductos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            try {
                // Fetch from repository (network)
                val productosRemotos = productoRepository.obtenerProductos()
                
                if (productosRemotos.isNotEmpty()) {
                    _fuenteMutable.value = productosRemotos
                } else {
                    // Fallback to demo data if network fails or returns empty
                    _fuenteMutable.value = emptyList()
                }

                // Update categories from loaded products
                _categorias.value = listOf("Todos") + 
                    _fuenteMutable.value.map { it.categoria }.distinct()

                // Carga favoritos de DB
                val favoritosEntities = favoritosRepository.obtenerTodos()
                val favoritosSet = favoritosEntities.map { it.id }.toSet()
                _favoritosIds.value = favoritosSet

                // Marca los productos favoritos
                _fuenteMutable.value = _fuenteMutable.value.map { p ->
                    p.copy(favorito = favoritosSet.contains(p.id))
                }
                aplicarFiltro()
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback on error
                _fuenteMutable.value = emptyList()
                _categorias.value = listOf("Todos")
            } finally {
                _ui.value = _ui.value.copy(loading = false)
            }
        }
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            val favoritosEntities = favoritosRepository.obtenerTodos()
            _favoritosIds.value = favoritosEntities.map { it.id }.toSet()
            // Actualizar la fuente para reflejar favoritos
            _fuenteMutable.value = _fuenteMutable.value.map { p ->
                p.copy(favorito = _favoritosIds.value.contains(p.id))
            }
            aplicarFiltro()
        }
    }
    fun actualizarContadorCarrito() {
        viewModelScope.launch {
            _cantidadCarrito.value = orderRepository.contarProductos()
        }
    }

    fun cargarCarrito() {
        viewModelScope.launch {
            val entidades = orderRepository.obtenerCarrito()
            val productos = _fuenteMutable.value

            _carrito.value = entidades.mapNotNull { entidad ->
                val producto = productos.find { it.id == entidad.productoId }
                producto?.let {
                    CartItem(producto = it, cantidad = entidad.cantidad)
                }
            }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        viewModelScope.launch {
            orderRepository.agregarAlCarrito(
                productoId = producto.id,
                nombre = producto.nombre,
                precioUnitario = producto.precio,
                cantidad = 1
            )
            actualizarContadorCarrito()
            cargarCarrito()
        }
    }

    fun eliminarDelCarrito(productoId: String) {
        viewModelScope.launch {
            orderRepository.eliminarDelCarrito(productoId)
            actualizarContadorCarrito()
            cargarCarrito()
        }
    }

    fun limpiarCarrito() {
        viewModelScope.launch {
            orderRepository.limpiarCarrito()
            actualizarContadorCarrito()
            cargarCarrito()
        }
    }

    fun totalCarrito(): Int {
        return _carrito.value.sumOf { it.subtotal }
    }

    fun actualizarCantidadCarrito(productoId: String, nuevaCantidad: Int) {
        viewModelScope.launch {
            orderRepository.actualizarCantidadCarrito(productoId, nuevaCantidad)
            actualizarContadorCarrito()
            cargarCarrito()
        }
    }


    // ---------- Acciones ----------
    fun setCategoria(cat: String) {
        _categoriaSel.value = cat
        aplicarFiltro()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                aplicarFiltro()
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(error = e.message ?: "Error al cargar productos")
            } finally {
                _ui.value = _ui.value.copy(loading = false)
            }
        }
    }

    fun actualizarProductoFavorito(producto: Producto) {
        viewModelScope.launch {

            val entity = ProductoFavoritoEntity(
                id = producto.id,
                nombre = producto.nombre,
                categoria = producto.categoria,
                unid = producto.unid,
                precio = producto.precio,
                imagenRes = producto.imagenRes
            )
            favoritosRepository.toggleFavorito(entity)

            // 🔹 Actualizar lista local de favoritos
            val favoritos = favoritosRepository.obtenerTodos().map { it.id }.toSet()
            _favoritosIds.value = favoritos

            // 🔹 Actualizar la fuente mutable
            _fuenteMutable.value = _fuenteMutable.value.map { p ->
                if (p.id == producto.id) {
                    p.copy(favorito = favoritos.contains(p.id))
                } else {
                    p
                }
            }
            aplicarFiltro()
        }
    }

    fun refreshHome() {
        _categoriaSel.value = "Todos"
        inicializarProductos()
    }

    fun logout() {
        _ui.value = _ui.value.copy(loading = true)
        viewModelScope.launch {
            authRepository.logout()
            _ui.value = _ui.value.copy(loading = false, loggedOut = true)
        }
    }

    fun resetLogoutState() {
        _ui.value = _ui.value.copy(loggedOut = false)
    }

    private fun aplicarFiltro() {
        val cat = _categoriaSel.value
        _productosFiltrados.value = if (cat == "Todos") {
            _fuenteMutable.value
        } else {
            _fuenteMutable.value.filter { it.categoria == cat }
        }
    }


}
