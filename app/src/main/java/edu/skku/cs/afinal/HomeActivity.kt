package edu.skku.cs.afinal

import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val titletext = findViewById<TextView>(R.id.titletext)
        titletext.text = "\n\n\n\n\nStart Exercising with physical activity"
        val combtn = findViewById<Button>(R.id.button)
        val btn1 = findViewById<Button>(R.id.textView43)
        val btn2 = findViewById<Button>(R.id.textView42)
        val logoutbtn = findViewById<Button>(R.id.button10)

        logoutbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btn1.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn2.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }

        combtn.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}