package ru.sicampus.bootcamp2025.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorityDto (
    @SerialName("id")
    val id: Long,
    @SerialName("authority")
    val authority: String
)