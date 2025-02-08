package ru.sicampus.bootcamp2025.domain.list

class GetUsersByDepartmentUserCase(
    private val repo: UserRepo
) {
    operator suspend fun invoke(
        departmentName: String,
        pageNum: Int,
        pageSize: Int
    ) = repo.getUsersByDepartmentName(departmentName, pageNum, pageSize)
}