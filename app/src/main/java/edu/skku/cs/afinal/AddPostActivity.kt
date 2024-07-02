package edu.skku.cs.afinal

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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val cancelbtn = findViewById<Button>(R.id.button3)
        val postbtn = findViewById<Button>(R.id.button4)
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editContent = findViewById<EditText>(R.id.editContent)

        val db = FirebaseFirestore.getInstance()
        val pageCollection = db.collection("post-page")

        postbtn.setOnClickListener {
            val title = editTitle.text.toString().trim()
            val content = editContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val currentDate = Calendar.getInstance().time
                val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentDate)
                val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("id", null)

                val post = hashMapOf(
                    "userid" to userId, // Replace with your actual method to get user id from session
                    "date" to formattedDate,
                    "title" to title,
                    "contents" to content,
                    "comcontents" to arrayListOf<HashMap<String, String>>() // Empty list initially
                )

                // Add new document (new post) in the DB
                pageCollection.add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, CommunityActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding post: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }

        cancelbtn.setOnClickListener {
            finish()
        }
    }
}
