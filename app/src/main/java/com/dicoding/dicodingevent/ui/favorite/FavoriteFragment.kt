package com.dicoding.dicodingevent.ui.favorite

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.dicodingevent.ListEventAdapter
import com.dicoding.dicodingevent.databinding.FragmentFavoriteBinding

import com.dicoding.dicodingevent.data.response.ListEventsItem
import com.dicoding.dicodingevent.ui.ViewModelFactory

import com.dicoding.dicodingevent.ui.detail.DetailActivity


class FavoriteFragment : Fragment() {


    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ListEventAdapter

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)


        adapter = ListEventAdapter { id ->
            val intent = Intent(requireContext() , DetailActivity::class.java)
            intent.putExtra("EVENT_ID" , id)
            startActivity(intent)
        }

        binding.rvFavoriteEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteEvents.adapter = adapter



        viewModel.getFavoritedEvents().observe(viewLifecycleOwner) { favorites ->
            val items = favorites.map {
                ListEventsItem(
                    id = it.id.toInt() ,
                    name = it.name ,
                    imageLogo = it.mediaCover ?: "" ,
                    summary = "" ,
                    category = "" ,
                    cityName = "" ,
                    description = "" ,
                    beginTime = "" ,
                    endTime = "" ,
                    link = "" ,
                    mediaCover = it.mediaCover ?: "" ,
                    ownerName = "" ,
                    quota = 0 ,
                    registrants = 0

                )
            }
            adapter.submitList(items)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

