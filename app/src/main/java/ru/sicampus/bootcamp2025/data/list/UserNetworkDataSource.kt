package ru.sicampus.bootcamp2025.data.list

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sicampus.bootcamp2025.data.Constants.serverIp
import ru.sicampus.bootcamp2025.data.Network

class UserNetworkDataSource {
    suspend fun getUsers(
        pageNum: Int,
        pageSize: Int,
        token: String
    ): Result<UserListPagingDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("$serverIp/api/person/paginated?page=$pageNum&size=$pageSize") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status ${result.status}")
            }
            result.body()
        }
    }
    suspend fun getUsersByDepartmentName(
        departmentName: String,
        token: String,
        pageNum: Int,
        pageSize: Int
    ): Result<UserListPagingDto> = withContext(Dispatchers.IO) {
        runCatching {
            val encodedDepartmentName = departmentName.encodeURLPath()
            val result = Network.client.get("$serverIp/api/person/$encodedDepartmentName/paginated?page=$pageNum&size=$pageSize") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status ${result.status}")
                Log.e("UserNetworkDataSource", "Status ${result.status}")
            }
            result.body()
        }
    }
}