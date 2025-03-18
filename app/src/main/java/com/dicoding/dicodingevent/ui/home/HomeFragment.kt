package com.dicoding.dicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dicoding.dicodingevent.R
import com.dicoding.dicodingevent.data.response.ListEventsItem
import com.dicoding.dicodingevent.databinding.FragmentHomeBinding
import com.dicoding.dicodingevent.ui.ViewModelFactory
import com.dicoding.dicodingevent.ui.detail.DetailActivity
import com.dicoding.dicodingevent.ui.detail.DetailActivity.Companion.EXTRA_EVENT_ID
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels { ViewModelFactory.getInstance(requireActivity()) }
    private lateinit var homeFinishedEventAdapter: HomeFinishedEventAdapter
    private lateinit var homeUpcomingEventAdapter: HomeUpcomingEventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Initialize adapters
        homeUpcomingEventAdapter = HomeUpcomingEventAdapter { event ->
            navigateToDetail(event.id.toString())
        }

        homeFinishedEventAdapter = HomeFinishedEventAdapter { event ->
            navigateToDetail(event.id.toString())
        }

        setupRecyclerViews()
        observeViewModel()
        loadData()
    }

    private fun setupRecyclerViews() {

        binding.rvUpcoming.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = homeUpcomingEventAdapter

            PagerSnapHelper().attachToRecyclerView(this)
        }


        binding.rvFinished.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = homeFinishedEventAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.upcomingEvents.observe(viewLifecycleOwner) { listEvents: List<ListEventsItem>? ->
                homeUpcomingEventAdapter.submitList(listEvents?.take(5) ?: emptyList())
            }

            // Observe finished events (maksimal 5 event)
            viewModel.finishedEvents.observe(viewLifecycleOwner) { listEvents: List<ListEventsItem>? ->
                homeFinishedEventAdapter.submitList(listEvents?.take(5) ?: emptyList())
            }

            // Observe loading state
            viewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoadingUpcoming ->
                binding.upcomingPB.visibility = if (isLoadingUpcoming) View.VISIBLE else View.GONE
            }

            viewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoadingFinished ->
                binding.finishedPB.visibility = if (isLoadingFinished) View.VISIBLE else View.GONE
            }


            // Observe error messages
            viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                if (!errorMessage.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData() {
        viewModel.loadUpcomingEvents()
        viewModel.loadFinishedEvents()
    }

    private fun navigateToDetail(eventId: String) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra(EXTRA_EVENT_ID, eventId)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
