package cl.duocuc.app.ui.register

import cl.duocuc.app.model.User
import cl.duocuc.app.repository.auth.AuthRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "onConfirmChange debe actualizar el estado de confirmación" {
        val repo = mockk<AuthRepository>(relaxed = true)
        val viewModel = RegisterViewModel(repo)

        viewModel.onConfirmChange("123456")
        viewModel.ui.value.confirm shouldBe "123456"
    }

    "submit debe fallar si las contraseñas no coinciden" {
        // Arrange
        val repo = mockk<AuthRepository>()
        val viewModel = RegisterViewModel(repo)

        // Llenamos datos validos pero contraseñas distintas
        viewModel.onEmailChange("test@duoc.cl")
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmChange("654321") // Distinta

        // Act
        viewModel.submit()

        // Assert
        viewModel.ui.value.error shouldBe "Las claves no coinciden"
    }

    "submit debe registrar usuario si todo es válido" {
        // Arrange
        val repo = mockk<AuthRepository>()
        // Usuario real que devolverá el repositorio
        val fakeUser = User(uid = "999", email = "nuevo@duoc.cl")

        coEvery { repo.signUp("nuevo@duoc.cl", "123456") } returns fakeUser

        val viewModel = RegisterViewModel(repo)
        viewModel.onEmailChange("nuevo@duoc.cl")
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmChange("123456") // Coinciden

        // Act
        viewModel.submit()
        dispatcher.scheduler.advanceUntilIdle() // Esperar corrutina

        // Assert
        viewModel.ui.value.registered shouldBe true
        viewModel.ui.value.user shouldBe fakeUser
        viewModel.ui.value.message shouldBe "Cuenta creada. Inicia sesión."
    }

    "submit debe mostrar error si el repositorio falla (ej: email ya existe)" {
        // Arrange
        val repo = mockk<AuthRepository>()
        // Simulamos que signUp devuelve null (fallo)
        coEvery { repo.signUp(any(), any()) } returns null

        val viewModel = RegisterViewModel(repo)
        viewModel.onEmailChange("duplicado@duoc.cl")
        viewModel.onPasswordChange("123456")
        viewModel.onConfirmChange("123456")

        // Act
        viewModel.submit()
        dispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.ui.value.registered shouldBe false
        viewModel.ui.value.error shouldBe "No se pudo crear la cuenta"
    }
})