package ru.sicampus.bootcamp2025.ui.list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.FragmentListBinding
import ru.sicampus.bootcamp2025.util.collectWithLifecycle

class ListFragment: Fragment(R.layout.fragment_list){
    private var _viewBinding: FragmentListBinding? = null
    private val viewBinding: FragmentListBinding get() = _viewBinding!!

    private val viewModel by viewModels<ListViewModel> { ListViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _viewBinding = FragmentListBinding.bind(view)
        val adapter = UserAdapter()

        viewBinding.content.adapter = adapter

        viewBinding.refresh.setOnClickListener { adapter.refresh() }

        val filterType = arguments?.getString("filter_type", "all") ?: "all"
        val departmentName = arguments?.getString("departmentName", "default") ?: "default"
        viewModel.setFilter(filterType)
        viewModel.setDepartmentName(departmentName) // FIXME()


        Log.d("ListFragment", "filter added")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedFilter.collect { filter ->
                    adapter.submitData(PagingData.empty())
                    when (filter) {
                        "all" -> viewModel.listState.collect { data -> adapter.submitData(data) }
                        "department" -> viewModel.departmentListState.collect { data -> adapter.submitData(data) }
                    }
                }
            }
        }

        viewModel.listState.collectWithLifecycle(this) { data ->
            adapter.submitData(data)
        }
        viewModel.departmentListState.collectWithLifecycle(this) { data ->
            adapter.submitData(data)
        }

        adapter.loadStateFlow.collectWithLifecycle(this) { data ->
            val state = data.refresh
            viewBinding.error.visibility =
                if (state is LoadState.Error) View.VISIBLE else View.GONE
            viewBinding.loading.visibility =
                if (state is LoadState.Error) View.VISIBLE else View.GONE
            if (state is LoadState.Error) {
                viewBinding.errorText.text = state.error.message.toString()
                Log.d("ListFragment", "${state.error.printStackTrace()}")
            }
        }

    }


//        viewModel.state.collectWithLifecycle(this) { state ->
//            viewBinding.error.visibility = if(state is ListViewModel.State.Error) View.VISIBLE else View.GONE
//            viewBinding.loading.visibility = if(state is ListViewModel.State.Error) View.VISIBLE else View.GONE
//            viewBinding.content.visibility = if(state is ListViewModel.State.Error) View.VISIBLE else View.GONE
//            when (state) {
//                is ListViewModel.State.Loading -> Unit
//                is ListViewModel.State.Show -> {
//                    adapter.submitList(state.items)
//                }
//                is ListViewModel.State.Error -> {
//                    viewBinding.text.text = state.text
//                }
//            }
//        }



    override fun onDestroy() {
        _viewBinding = null
        super.onDestroy()
    }
}