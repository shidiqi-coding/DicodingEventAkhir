package com.dicoding.dicodingevent.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.dicodingevent.R


import com.dicoding.dicodingevent.databinding.ActivityDetailBinding
import com.dicoding.dicodingevent.entity.FavoriteDatabase
import com.dicoding.dicodingevent.entity.FavoriteEvent

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var isFavorite = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: ""

        val database = FavoriteDatabase.getInstance(this)
        val favoriteEventDao = database.favoriteEventDao()

        val factory = DetailViewModelFactory(favoriteEventDao)



        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(null)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    setupFabAnimation()
    setupFavoriteButton()
    setEventData()

    viewModel.getDetailEvents(eventId)
}

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupFabAnimation() {
        val fabOffset = binding.toolbar.height.toFloat() + binding.fabFavorite.height / 2

        binding.appBarLayout.addOnOffsetChangedListener { _ , verticalOffset ->
            val isCollapsed = abs(verticalOffset) >= binding.appBarLayout.totalScrollRange
            binding.fabFavorite.animate().translationY(if (isCollapsed) -fabOffset else 0f)
                .setDuration(200).start()
        }
    }

    private fun setupFavoriteButton() {
        binding.fabFavorite.setOnClickListener{
            viewModel.detailEvent.value?.let { event ->
                val favoriteEvent = FavoriteEvent (
                    id = event.id.toLong(),
                    name = event.name,
                    mediaCover = event.mediaCover
                )
                viewModel.toggleFavorite(favoriteEvent)
                updateFavoriteButton()


            }
        }
    }

    private fun updateFavoriteButton() {
        binding.fabFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    private fun setEventData() {
        viewModel.detailEvent.observe(this) { event ->
            val totalQuota = event.quota - event.registrants
            with(binding) {
                tvOwnerName.text = event.ownerName
                tvEventDescription.text =
                    HtmlCompat.fromHtml(event.description , HtmlCompat.FROM_HTML_MODE_LEGACY)
                        .toString()
                tvTitle.text = event.name
                tvQuota.text = totalQuota.toString()
                tvBeginTime.text = dateFormat(event.beginTime , event.endTime)

                Glide.with(this@DetailActivity).load(event.mediaCover).into(ivMediaCover)

                btnRegister.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW , Uri.parse(event.link))
                    startActivity(intent)
                }

                collapsingToolbar.title = event.name
            }
        }

        viewModel.isLoading.observe(this) { showLoading(it) }
        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this , errorMessage , Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun dateFormat(beginTime: String , endTime: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss" , Locale.US)
        val output = SimpleDateFormat("dd MM yyyy, HH:mm" , Locale.US)
        return try {
            val beginDate = input.parse(beginTime)
            val endDate = input.parse(endTime)
            "${beginDate?.let { output.format(it) }} - ${endDate?.let { output.format(it) }}"
        } catch (e: ParseException) {
            e.printStackTrace()
            "$beginTime - $endTime"
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"

        fun start(context: Context , eventId: String) {
            val intent = Intent(context , DetailActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID , eventId)
            context.startActivity(intent)
        }
    }
}
