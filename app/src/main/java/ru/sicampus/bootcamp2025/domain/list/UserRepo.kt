package ru.sicampus.bootcamp2025.domain.list

interface UserRepo {
    suspend fun getUsers(
        pageNum: Int,
        pageSize: Int
    ): Result<List<UserEntity>>
    suspend fun getUsersByDepartmentName(
        departmentName: String,
        pageNum: Int,
        pageSize: Int
    ): Result<List<UserEntity>>
}