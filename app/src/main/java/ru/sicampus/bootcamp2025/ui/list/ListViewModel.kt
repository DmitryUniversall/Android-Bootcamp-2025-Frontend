package ru.sicampus.bootcamp2025.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.sicampus.bootcamp2025.data.auth.storage.AuthStorageDataSource
import ru.sicampus.bootcamp2025.data.list.UserNetworkDataSource
import ru.sicampus.bootcamp2025.data.list.UserRepoImpl
import ru.sicampus.bootcamp2025.domain.list.GetUsersByDepartmentUserCase
import ru.sicampus.bootcamp2025.domain.list.GetUsersUseCases

class ListViewModel(
    private val getUsersUseCases: GetUsersUseCases,
    private val getUsersByDepartmentUserCase: GetUsersByDepartmentUserCase,
): ViewModel(){
    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter = _selectedFilter.asStateFlow()

    private var departmentName: String = "" // FIXME()

    fun setDepartmentName(depName: String){
        departmentName = depName
    }
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }
    val listState = Pager(
        config = PagingConfig(pageSize = 20,
        enablePlaceholders = false,
        maxSize = 100
        )
    ) {
        ListPagingSource(getUsersUseCases::invoke)
    }.flow
        .cachedIn(viewModelScope)

    val departmentListState = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false, maxSize = 100)
    ) {
        ListPagingSource { pageNum, pageSize ->
            Log.d("123", "${departmentName.toString()}")
            getUsersByDepartmentUserCase.invoke("$departmentName", pageNum, pageSize)
        }
    }.flow.cachedIn(viewModelScope)

    companion object {
        var Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = UserRepoImpl(
                    userNetworkDataSource = UserNetworkDataSource(),
                    authStorageDataSource = AuthStorageDataSource
                )
                return ListViewModel(

                    getUsersUseCases = GetUsersUseCases(
                        repo
                    ),
                    getUsersByDepartmentUserCase = GetUsersByDepartmentUserCase(
                        repo
                    )
                ) as T
            }
        }
    }

}