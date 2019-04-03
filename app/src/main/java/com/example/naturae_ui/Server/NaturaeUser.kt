package com.example.naturae_ui.Server

import java.io.Serializable

data class NaturaeUser(val firstName: String?,
                       val lastName: String?,
                       val email: String?,
                       val accessToken: String?,
                       val refreshToken: String?,
                       val profileImage: String?): Serializable