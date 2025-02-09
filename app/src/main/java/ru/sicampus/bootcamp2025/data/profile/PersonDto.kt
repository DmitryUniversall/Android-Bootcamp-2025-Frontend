package ru.sicampus.bootcamp2025.data.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.sicampus.bootcamp2025.domain.profile.AuthorityEntity

@Serializable
data class PersonDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("username")
    val login: String,
    @SerialName("email")
    val email: String,
    @SerialName("photoUrl")
    val photoUrl: String,
    @SerialName("info")
    val info: String?,
    @SerialName("phone")
    val phone: String?,
    @SerialName("departmentName")
    val departmentName: String?,
    @SerialName("authorities")
    val authorities: List<AuthorityDto>,
)