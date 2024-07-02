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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val login = findViewById<Button>(R.id.textView3)
        val submit = findViewById<Button>(R.id.button)
        val idEditText = findViewById<EditText>(R.id.editTextText)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val nameEditText = findViewById<EditText>(R.id.textView)

        submit.setOnClickListener {
            val name = nameEditText.text.toString()
            val id = idEditText.text.toString()
            if (id.length > 10) {
                Toast.makeText(this, "ID length must be less than 10 characters", Toast.LENGTH_SHORT).show()
                clearInputFields()
            } else {
                val password = passwordEditText.text.toString()
                val db = Firebase.firestore
                val userCollection = db.collection("user-info")

                val queryRef = userCollection.whereEqualTo("id", id)
                queryRef.get().addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        Toast.makeText(this, "This ID already exists", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        val userDocRef = db.collection("workout-record").document(id)
                        userDocRef.set(hashMapOf("details" to emptyMap<String, Any>()))

                        // Store user id in SharedPreferences
                        val sharedPreferences =
                            getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("id", id)
                        editor.apply()

                        // Add user information to the 'user-info' collection
                        val userData = hashMapOf(
                            "name" to name,
                            "id" to id,
                            "password" to password,
                            "greet" to "Let's workout!"
                        )

                        userCollection.add(userData).addOnSuccessListener {
                            // Move to main page
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error adding document: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error getting documents: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun clearInputFields() {
        findViewById<EditText>(R.id.textView).text.clear()
        findViewById<EditText>(R.id.editTextText).text.clear()
        findViewById<EditText>(R.id.editTextPassword).text.clear()
    }
}