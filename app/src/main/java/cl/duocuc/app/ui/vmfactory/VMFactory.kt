package cl.duocuc.app.ui.vmfactory


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.local.favoritos.ProductoFavoritoDao
import cl.duocuc.app.data.media.MediaRepository
import cl.duocuc.app.data.repository.OrderRepository
import cl.duocuc.app.data.repository.ProductoRepository
import cl.duocuc.app.repository.RecordatorioRepository
import cl.duocuc.app.repository.auth.AuthRepository
import cl.duocuc.app.repository.auth.FavoritosRepository
import cl.duocuc.app.repository.auth.FirebaseAuthDataSource
import cl.duocuc.app.ui.principal.PrincipalViewModel
import cl.duocuc.app.ui.profile.ProfileViewModel
import cl.duocuc.app.ui.recordatorio.RecordatorioViewModel


class ProfileVMFactory(
    private val authDs: FirebaseAuthDataSource,
    private val mediaRepo: MediaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = when (modelClass) {
            ProfileViewModel::class.java -> ProfileViewModel(authDs, mediaRepo)
            else -> error("VM no soportado: $modelClass")
        }
        @Suppress("UNCHECKED_CAST")
        return vm as T
    }
}


class PrincipalVMFactory(
    private val db: AppDatabase,
    private val dao: ProductoFavoritoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrincipalViewModel::class.java)) {
            val orderRepo = OrderRepository(db)
            val productoRepo = ProductoRepository(db)
            val authRepo = AuthRepository()
            val favRepo = FavoritosRepository(dao)
            @Suppress("UNCHECKED_CAST")
            return PrincipalViewModel(orderRepo, productoRepo, authRepo, favRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}