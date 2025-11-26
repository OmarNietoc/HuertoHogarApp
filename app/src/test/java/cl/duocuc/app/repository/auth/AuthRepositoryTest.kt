package cl.duocuc.app.repository.auth

import cl.duocuc.app.data.network.ShoppyApiService
import cl.duocuc.app.data.network.dto.UserRequestDto
import cl.duocuc.app.model.User
import com.google.firebase.auth.FirebaseUser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryTest : StringSpec({

    val mockDataSource = mockk<FirebaseAuthDataSource>(relaxed = true)
    val mockApiService = mockk<ShoppyApiService>(relaxed = true)

    // CAMBIO 1: Hacemos que el usuario sea 'relaxed' para que no explote si se borra
    val fakeFirebaseUser = mockk<FirebaseUser>(relaxed = true)

    beforeTest {
        clearAllMocks()

        // CAMBIO 2: Configuramos el usuario SIEMPRE después de limpiar
        // Así nos aseguramos de que tenga datos frescos en cada test
        every { fakeFirebaseUser.uid } returns "firebase_123"
        every { fakeFirebaseUser.email } returns "test@duoc.cl"
    }

    "login exitoso debe retornar un User cuando Firebase y API responden bien" {
        // Arrange
        coEvery { mockDataSource.signIn("test@duoc.cl", "123456") } returns fakeFirebaseUser
        // Usamos 'any()' para que no falle por diferencia de strings exactos
        coEvery { mockApiService.getUserByEmail(any()) } returns mockk()

        val repository = AuthRepository(mockDataSource, mockApiService)

        // Act
        val result = repository.login("test@duoc.cl", "123456")

        // Assert
        result shouldNotBe null
        result?.email shouldBe "test@duoc.cl"
    }

    "login debe intentar crear usuario en API si recibe un 404 (Sincronización)" {
        coEvery { mockDataSource.signIn(any(), any()) } returns fakeFirebaseUser

        val error404 = HttpException(Response.error<Any>(404, okhttp3.ResponseBody.create(null, "")))
        coEvery { mockApiService.getUserByEmail(any()) } throws error404

        val repository = AuthRepository(mockDataSource, mockApiService)

        repository.login("test@duoc.cl", "123456")

        // Assert
        coVerify(exactly = 1) { mockApiService.addUser(any()) }
    }

    "login debe retornar null si Firebase falla" {
        coEvery { mockDataSource.signIn(any(), any()) } returns null

        val repository = AuthRepository(mockDataSource, mockApiService)
        val result = repository.login("bad@mail.com", "wrongpass")

        result shouldBe null

        coVerify(exactly = 0) { mockApiService.getUserByEmail(any()) }
    }

    "signUp debe llamar a addUser en la API" {
        coEvery { mockDataSource.signUp(any(), any()) } returns fakeFirebaseUser
        val repository = AuthRepository(mockDataSource, mockApiService)

        repository.signUp("nuevo@duoc.cl", "123456")

        coVerify(exactly = 1) { mockApiService.addUser(any()) }
    }
})