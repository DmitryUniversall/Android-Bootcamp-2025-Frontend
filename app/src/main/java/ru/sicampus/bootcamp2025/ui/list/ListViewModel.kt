package ru.sicampus.bootcamp2025.ui.list

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
//    private val _state = MutableStateFlow<State>(State.Loading)
//    public val state = _state.asStateFlow()
    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter = _selectedFilter.asStateFlow()

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
            getUsersByDepartmentUserCase.invoke("SomeDepartment", pageNum, pageSize)
        }
    }.flow.cachedIn(viewModelScope)
//    init {
//        updateState()
//    }
//
//    fun clickRefresh(){
//        updateState()
//    }
//
//    private fun updateState() {
//        viewModelScope.launch {
//            _state.emit(State.Loading)
//            _state.emit(
//                getUsersUseCases.invoke().fold(
//                    onSuccess = { data ->
//                        State.Show(data)
//                    },
//                    onFailure = { error ->
//                        State.Error(error.message.toString())
//                    }
//                )
//            )
//        }
//    }

//    sealed interface State {
//        data object Loading: State
//        data class Show(
//            val items: List<UserEntity>
//        ): State
//        data class Error(
//            val text: String
//        ): State
//    }

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