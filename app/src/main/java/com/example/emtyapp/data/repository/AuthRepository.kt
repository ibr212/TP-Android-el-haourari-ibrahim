package com.example.emtyapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.emtyapp.data.model.User
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Simulation d'une base de données locale
    private val users = mutableMapOf<String, User>()

    suspend fun login(email: String, password: String): Result<User> {
        delay(1000) // Simulation d'un appel réseau

        // Vérification simple - à remplacer par votre logique
        if (email == "test@test.com" && password == "password") {
            val user = User(
                id = "1",
                email = email,
                name = "Utilisateur Test"
            )
            saveUserSession(user)
            return Result.success(user)
        }

        // Vérifier dans les utilisateurs enregistrés
        val user = users.values.find { it.email == email }
        if (user != null) {
            saveUserSession(user)
            return Result.success(user)
        }

        return Result.failure(Exception("Email ou mot de passe incorrect"))
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        delay(1000) // Simulation d'un appel réseau

        // Vérifier si l'email existe déjà
        if (users.values.any { it.email == email }) {
            return Result.failure(Exception("Cet email est déjà utilisé"))
        }

        val user = User(
            id = System.currentTimeMillis().toString(),
            email = email,
            name = name
        )

        users[user.id] = user
        saveUserSession(user)
        return Result.success(user)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getCurrentUser(): User? {
        val id = prefs.getString("user_id", null) ?: return null
        val email = prefs.getString("user_email", null) ?: return null
        val name = prefs.getString("user_name", null) ?: return null

        return User(id = id, email = email, name = name)
    }

    fun isLoggedIn(): Boolean = getCurrentUser() != null

    private fun saveUserSession(user: User) {
        prefs.edit()
            .putString("user_id", user.id)
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .apply()
    }
}
