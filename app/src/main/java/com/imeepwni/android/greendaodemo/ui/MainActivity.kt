package com.imeepwni.android.greendaodemo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.imeepwni.android.greendaodemo.R
import com.imeepwni.android.greendaodemo.app.App
import com.imeepwni.android.greendaodemo.databinding.ActivityMainBinding
import com.imeepwni.android.greendaodemo.viewmodel.DBActionViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewmodel = DBActionViewModel(this, (application as App).getDaoSession())
    }
}
