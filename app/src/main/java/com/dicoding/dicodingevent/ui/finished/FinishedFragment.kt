package com.dicoding.dicodingevent.ui.finished


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.dicodingevent.R

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.dicodingevent.ListEventAdapter

import com.dicoding.dicodingevent.databinding.FragmentFinishedBinding
import com.dicoding.dicodingevent.ui.detail.DetailActivity


class FinishedFragment : Fragment(R.layout.fragment_finished) {

    //
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinishedViewModel by viewModels()
    private lateinit var adapter: ListEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinishedBinding.inflate(inflater , container , false)
        return binding.root
    }


    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupSearchView()

        viewModel.fetchFinishedEvents()
    }

    private fun setupRecyclerView() {
        adapter = ListEventAdapter { event ->
            DetailActivity.start(requireContext() , event)
        }
        binding.rvListFin.apply {
            layoutManager = StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
            adapter = this@FinishedFragment.adapter

        }
    }

    private fun setupSearchView() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { textView, actionId, event ->
                searchBar.setText(searchView.text)
                searchView.hide()
                viewModel.searchEvents(searchView.text.toString())
                false
            }

        }
    }


    private fun observeViewModel() {
        viewModel.eventlist.observe(viewLifecycleOwner)  {events ->
            adapter.submitList(events)
        }

        viewModel.loading.observe(viewLifecycleOwner) {loading ->
            showLoading(loading)
        }



    }



    private fun showLoading(loading: Boolean){
        binding.finishedPB.visibility = if(loading) View.VISIBLE else View.GONE
    }

}






