package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
    }

    private fun setupView() {
        val storyId = intent.getStringExtra(EXTRA_ID) ?: return
        val token = intent.getStringExtra(EXTRA_TOKEN) ?: return

        viewModel.getDetailStory(token, storyId)
    }

    private fun setupViewModel() {
        viewModel.storyDetail.observe(this) { story ->
            binding.apply {
                tvName.text = story.name
                tvDescription.text = story.description
                tvCreatedAt.text = story.createdAt
                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .into(ivStory)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TOKEN = "extra_token"
    }
}