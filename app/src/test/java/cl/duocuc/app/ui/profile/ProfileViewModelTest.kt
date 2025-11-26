package cl.duocuc.app.ui.profile

import android.content.Context
import android.net.Uri
import cl.duocuc.app.data.media.MediaRepository
import cl.duocuc.app.repository.auth.FirebaseAuthDataSource
import com.google.firebase.auth.FirebaseUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    // Mocks de las dependencias
    val authRepo = mockk<FirebaseAuthDataSource>(relaxed = true)
    val mediaRepo = mockk<MediaRepository>(relaxed = true)

    // Mocks de clases de Android (Para que no falle por "Method not mocked")
    val mockContext = mockk<Context>(relaxed = true)
    val mockUri = mockk<Uri>(relaxed = true)

    beforeTest {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "Al iniciar (init), debe cargar los datos del usuario si está logueado" {
        // Arrange
        val fakeUser = mockk<FirebaseUser>()
        every { fakeUser.uid } returns "123"
        every { fakeUser.email } returns "profe@duoc.cl"
        every { fakeUser.displayName } returns "Profesor"

        // IMPORTANTE: Configurar el mock ANTES de instanciar el ViewModel
        // porque el código del init{} se ejecuta inmediatamente al crear la clase.
        every { authRepo.currentUser() } returns fakeUser

        // Act
        val viewModel = ProfileViewModel(authRepo, mediaRepo)

        // Assert
        val state = viewModel.ui.value
        state.uid shouldBe "123"
        state.email shouldBe "profe@duoc.cl"
        state.displayName shouldBe "Profesor"
    }

    "setLastSavedPhoto debe actualizar el uri en el estado" {
        val viewModel = ProfileViewModel(authRepo, mediaRepo)

        // Usamos el mockUri que creamos arriba
        viewModel.setLastSavedPhoto(mockUri)

        viewModel.ui.value.lastSavedPhoto shouldBe mockUri
    }

    "setError debe actualizar el mensaje de error" {
        val viewModel = ProfileViewModel(authRepo, mediaRepo)

        viewModel.setError("Error de prueba")

        viewModel.ui.value.error shouldBe "Error de prueba"
    }

    "createDestinationUriForCurrentUser debe llamar al repositorio si hay UID" {
        // Arrange
        // CORRECCIÓN: Agregamos (relaxed = true) para que no falle al leer email/nombre en el init
        val fakeUser = mockk<FirebaseUser>(relaxed = true) {
            every { uid } returns "user_999"
        }

        every { authRepo.currentUser() } returns fakeUser
        every { mediaRepo.createImageUriForUser(any(), "user_999") } returns mockUri

        val viewModel = ProfileViewModel(authRepo, mediaRepo)

        // Act
        val resultUri = viewModel.createDestinationUriForCurrentUser(mockContext)

        // Assert
        resultUri shouldBe mockUri
        verify(exactly = 1) { mediaRepo.createImageUriForUser(mockContext, "user_999") }
    }

    "createDestinationUriForCurrentUser debe retornar null si no hay usuario (UID null)" {
        // Arrange
        // Simulamos que NO hay usuario logueado
        every { authRepo.currentUser() } returns null

        val viewModel = ProfileViewModel(authRepo, mediaRepo)

        // Act
        val resultUri = viewModel.createDestinationUriForCurrentUser(mockContext)

        // Assert
        resultUri shouldBe null
        // Verificamos que NUNCA se llamó al repo de media
        verify(exactly = 0) { mediaRepo.createImageUriForUser(any(), any()) }
    }
})