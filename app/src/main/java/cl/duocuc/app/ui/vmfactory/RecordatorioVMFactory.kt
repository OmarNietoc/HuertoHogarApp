package cl.duocuc.app.ui.vmfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duocuc.app.data.local.AppDatabase
import cl.duocuc.app.data.media.MediaRepository
import cl.duocuc.app.repository.auth.FirebaseAuthDataSource
import cl.duocuc.app.ui.profile.ProfileViewModel
import kotlin.collections.get
import cl.duocuc.app.ui.recordatorio.RecordatorioViewModel
import cl.duocuc.app.repository.RecordatorioRepository


class RecordatorioVMFactory(
    context: Context,
    private val uid: String
) : ViewModelProvider.Factory {

    private val repo by lazy {
        val db = AppDatabase.get(context)
        RecordatorioRepository(db.reminderDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordatorioViewModel(repo, uid) as T
    }
}