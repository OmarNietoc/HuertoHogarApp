package cl.duocuc.app.ui.vmfactory


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.media.MediaRepository
import cl.duocuc.app.repository.RecordatorioRepository
import cl.duocuc.app.repository.auth.FirebaseAuthDataSource
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