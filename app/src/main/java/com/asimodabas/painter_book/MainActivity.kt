package com.asimodabas.painter_book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.asimodabas.painter_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun saveButtonClicked(){


    }

    fun selectImage(){


    }

}