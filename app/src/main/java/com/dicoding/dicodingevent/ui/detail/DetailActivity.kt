package com.dicoding.dicodingevent.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.dicoding.dicodingevent.R
import android.view.View
import android.widget.ImageView
//import android.widget.ImageView


//import android.widget.ImageView
//import android.widget.TextView
import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat

//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.dicodingevent.data.response.Event
import com.dicoding.dicodingevent.databinding.ActivityDetailBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response/
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var isFavorite = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)


        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: ""

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(null)

        val backButton: ImageView = findViewById(R.id.btnBack)
        backButton.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }


        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupFavoriteButton()
        setEventData()

        viewModel.getDetailEvents(eventId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupFavoriteButton(){
        val fabFavorite: FloatingActionButton = binding.fabFavorite

//        val eventData = intent.getStringExtra(EXTRA_EVENT_ID)
//        isFavorite = eventData != null

        isFavorite = false
        updateFavoriteButton(fabFavorite)


        fabFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteButton(fabFavorite)
        }
    }

    private fun updateFavoriteButton(fabFavorite: FloatingActionButton){
        if(isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorite)
//            fabFavorite.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.muted_blue_violet)

        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border)
//            fabFavorite.backgroundTintList =
//                ContextCompat.getColorStateList(this, R.color.muted_blue_violet)

        }
    }

    private fun setInitialEventData(event: Event) {
        with(binding) {
            tvTitle.text = event.name
            tvEventDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY) ?: "Deskripsi tidak tersedia"

            Glide.with(this@DetailActivity)
                .load(event.imageLogo)
                //.placeholder(R.drawable.placeholder_image) // Placeholder saat gambar loading
                //.error(R.drawable.error_image) // Jika gambar gagal dimuat
                .into(ivMediaCover)
        }
    }



    private fun setEventData() {
        viewModel.detailEvent.observe(this) { event ->

            val totalQuota = event.quota - event.registrants
            with(binding) {
                tvOwnerName.text = event.ownerName
                tvEventDescription.text =
                    HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                tvTitle.text = event.name
                tvQuota.text = totalQuota.toString()
                tvBeginTime.text = dateFormat(event.beginTime, event.endTime)
                Glide.with(this@DetailActivity)
                    .load(event.mediaCover)
                    .into(binding.ivMediaCover)

                btnRegister.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }

                collapsingToolbar.title = event.name
                setInitialEventData(event)

            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun dateFormat(beginTime: String, endTime: String): String {
        val input = SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US)
        val output = SimpleDateFormat("dd mm yyyy, HH:mm", Locale.US)
        try {
            val beginDate = input.parse(beginTime)
            val endDate = input.parse(endTime)
            return "${beginDate?.let { output.format(it) }} - ${endDate?.let {
                output.format(
                    it
                )
            }}"
        } catch (e: ParseException) {
            e.printStackTrace()
            return "$beginTime - $endTime"
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            context.startActivity(intent)
        }
    }




}