package cl.duocuc.app.ui.login

import cl.duocuc.app.repository.auth.AuthRepository
import cl.duocuc.app.model.User
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest {
        Dispatchers.setMain(dispatcher)
        // ¡YA NO NECESITAMOS mockkStatic NI Patterns!
        // Ahora el test corre 100% en Kotlin puro.
    }

    afterTest {
        Dispatchers.resetMain()
        unmockkAll()
    }

    "onEmailChange debe actualizar el estado del email" {
        val repo = mockk<AuthRepository>(relaxed = true)
        val viewModel = LoginViewModel(repo)
        viewModel.onEmailChange("profe@duoc.cl")
        viewModel.ui.value.email shouldBe "profe@duoc.cl"
    }

    "onPasswordChange debe actualizar el estado del password" {
        val repo = mockk<AuthRepository>(relaxed = true)
        val viewModel = LoginViewModel(repo)
        viewModel.onPasswordChange("123456")
        viewModel.ui.value.password shouldBe "123456"
    }

    "submit con password corto debe retornar error en el estado" {
        val repo = mockk<AuthRepository>(relaxed = true)
        val viewModel = LoginViewModel(repo)

        viewModel.onEmailChange("test@correo.cl")
        viewModel.onPasswordChange("123")
        viewModel.submit()

        viewModel.ui.value.error shouldBe "La clave debe tener al menos 6 caracteres"
    }

    "submit con credenciales correctas debe loguear al usuario" {
        val repo = mockk<AuthRepository>()
        val fakeUser = User(
            uid = "12345",
            email = "test@duoc.cl",
            displayName = "Estudiante Duoc"
        )
        coEvery { repo.login("test@duoc.cl", "123456") } returns fakeUser

        val viewModel = LoginViewModel(repo)
        viewModel.onEmailChange("test@duoc.cl")
        viewModel.onPasswordChange("123456")

        viewModel.submit()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.ui.value.loggedIn shouldBe true
        viewModel.ui.value.user shouldBe fakeUser
    }

    "submit con credenciales incorrectas debe mostrar error" {
        val repo = mockk<AuthRepository>()
        coEvery { repo.login(any(), any()) } returns null

        val viewModel = LoginViewModel(repo)
        viewModel.onEmailChange("error@duoc.cl")
        viewModel.onPasswordChange("123456")

        viewModel.submit()

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.ui.value.loggedIn shouldBe false
        viewModel.ui.value.error shouldBe "Error al iniciar sesión"
    }
})