package ru.sicampus.bootcamp2025.data.list

import ru.sicampus.bootcamp2025.data.auth.AuthStorageDataSource
import ru.sicampus.bootcamp2025.domain.list.UserEntity
import ru.sicampus.bootcamp2025.domain.list.UserRepo

class UserRepoImpl(
    private val userNetworkDataSource: UserNetworkDataSource,
    private val authStorageDataSource: AuthStorageDataSource
): UserRepo {
    override suspend fun getUsers(): Result<List<UserEntity>> {
        val token = authStorageDataSource.token
            ?: return Result.failure(IllegalStateException("token is null"))
        return userNetworkDataSource.getUsers(token).map { listDto ->
            listDto.mapNotNull { dto ->
                UserEntity(
                    name = dto.name ?: return@mapNotNull null,
                    email = dto.email ?: return@mapNotNull null,
                    photoUrl = dto.photoUrl?: return@mapNotNull null
                )
            }

        }
    }

}