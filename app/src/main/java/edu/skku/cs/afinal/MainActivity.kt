package edu.skku.cs.afinal

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.firestore

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        // ... other initialization code
    }
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val signup = findViewById<Button>(R.id.textView3)
        val submit = findViewById<Button>(R.id.button)
        val id = findViewById<EditText>(R.id.editTextText)
        val pass = findViewById<EditText>(R.id.editTextPassword)

        val db = Firebase.firestore

        val userCollection = db.collection("user-info")

        submit.setOnClickListener {
            val idtxt = id.text.toString()
            val password = pass.text.toString()
            val queryRef = userCollection.whereEqualTo("id", idtxt).whereEqualTo("password", password)
            queryRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.size() > 0) {
                        // 로그인 성공 시 SharedPreferences에 사용자 ID 저장
                        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("id", idtxt)
                        editor.apply()

                        // 메인 페이지로 리디렉션
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 액티비티 종료
                    } else {
                        // 로그인 실패 시 알림
                        Toast.makeText(this, "Invalid ID or password", Toast.LENGTH_SHORT).show()
                        id.text.clear()
                        pass.text.clear()
                    }
                }
                .addOnFailureListener { exception ->
                    // 쿼리 실패 시 오류 로그 출력
                    Toast.makeText(this, "Error getting documents: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }


        signup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
            finish()
        }
    }
}