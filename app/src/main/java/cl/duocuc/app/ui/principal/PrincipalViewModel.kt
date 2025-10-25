package cl.duocuc.app.ui.principal


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duocuc.app.model.Producto
import cl.duocuc.app.model.productosDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel : ViewModel() {

    // ---------- Fuente mutable ----------
    private val fuente: List<Producto> = productosDemo
    private val _fuenteMutable = MutableStateFlow(fuente)  // la fuente "inmutable" original queda aquí
    private val fuenteFlow: StateFlow<List<Producto>> = _fuenteMutable.asStateFlow()
    val todosProductos: StateFlow<List<Producto>> = _fuenteMutable.asStateFlow()

    // ---------- Categorías ----------
    val categorias: List<String> = listOf("Todos") +
            _fuenteMutable.value.map { it.categoria }.distinct()

    // ---------- Estado general ----------
    private val _ui = MutableStateFlow(PrincipalUiState())
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    private val _categoriaSel = MutableStateFlow("Todos")
    val categoriaSel: StateFlow<String> = _categoriaSel.asStateFlow()

    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

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
        // 🔹 actualizar mutable
        _fuenteMutable.value = _fuenteMutable.value.map {
            if (it.id == producto.id) producto else it
        }
        aplicarFiltro()
    }

    fun refreshHome() {
        _categoriaSel.value = "Todos"
        cargarProductos()
    }

    fun logout() {
        _ui.value = _ui.value.copy(loading = true)
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = false, loggedOut = true)
        }
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
