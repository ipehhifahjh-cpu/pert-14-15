package com.example

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.industri.fleettrack.ui.ManifestActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, ManifestActivity::class.java))
        finish()
    }
}
