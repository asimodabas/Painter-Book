package com.asimodabas.painter_book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.asimodabas.painter_book.databinding.ActivityDetailsBinding
import com.asimodabas.painter_book.databinding.ActivityMainBinding

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

}