package com.example.bossdrop.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.Exclude
/**
 * Modelo de dados para o Usuário, que será salvo na coleção 'users'.
 */
data class User(
    // ID único fornecido pelo Firebase Authentication (ID do documento)
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("username") @set:PropertyName("username")
    var username: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    // URL da imagem de perfil (opcional, para ser implementado com Firebase Storage depois)
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl")
    var profileImageUrl: String? = null,

    // IDs dos jogos favoritos
    @get:PropertyName("favoriteGameIds") @set:PropertyName("favoriteGameIds")
    var favoriteGameIds: List<String> = emptyList(),

    @get:Exclude @set:Exclude
    var providerId: String = "password"
)