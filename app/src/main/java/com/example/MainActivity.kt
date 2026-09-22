package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.data.BasokaRepository
import com.example.ui.MainAppScaffold
import com.example.ui.theme.BasokaTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: BasokaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = BasokaRepository(applicationContext)

        setContent {
            BasokaTheme {
                MainAppScaffold(repository = repository)
            }
        }
    }
}

