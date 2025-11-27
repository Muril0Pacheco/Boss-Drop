package com.murilo.bossdrop.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.Exclude

data class User(
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("username") @set:PropertyName("username")
    var username: String = "",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    // URL da imagem de perfil (não implementado atualmente. Possivelmente no futuro)
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl")
    var profileImageUrl: String? = null,

    @get:PropertyName("favoriteGameIds") @set:PropertyName("favoriteGameIds")
    var favoriteGameIds: List<String> = emptyList(),

    @get:Exclude @set:Exclude
    var providerId: String = "password"
)