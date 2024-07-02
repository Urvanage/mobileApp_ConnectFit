package edu.skku.cs.afinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

data class User(
    val id: String,
    val password: String,
    var greet: String,
    var workcnt: Int,
    val name: String,
) : Serializable

class UserAdapter(
    val context: Context,
    val data: ArrayList<User>,
    val loginedUser: String?
) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): Any {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val generatedView = inflater.inflate(R.layout.user_record, null)

        val idtv = generatedView.findViewById<TextView>(R.id.textView25)
        val workcnttv = generatedView.findViewById<TextView>(R.id.textView26)
        val greettv = generatedView.findViewById<EditText>(R.id.textView27)
        val viewRecordButton = generatedView.findViewById<Button>(R.id.button5)

        idtv.text = data[p0].id
        workcnttv.text = data[p0].workcnt.toString()
        greettv.setText(data[p0].greet)

        // loginedUser와 idtv.text 비교하여 EditText 속성 설정
        if (loginedUser == data[p0].id) {
            greettv.isFocusable = true
            greettv.isFocusableInTouchMode = true
            greettv.isCursorVisible = true
            greettv.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = greettv.text.toString()
                    if (text.length > 0 && text.length <= 48) {
                        val db = FirebaseFirestore.getInstance()
                        val userCollection = db.collection("user-info")
                        val query = userCollection.whereEqualTo("id", loginedUser)
                        query.get().addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val userDocRef = querySnapshot.documents[0].reference
                                userDocRef.update("greet", text)
                                    .addOnSuccessListener {
                                        //Toast.makeText(context, "Greeting updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Error updating greeting: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "User document does not exist.", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Error querying user document: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else if (text.length > 48) {
                        Toast.makeText(context, "You can't enter more than 48 characters", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            greettv.isFocusable = false
            greettv.isFocusableInTouchMode = false
            greettv.isCursorVisible = false
            greettv.keyListener = null
        }
        greettv.textSize = 16f  // 적절한 텍스트 크기로 설정


        viewRecordButton.setOnClickListener {
            val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("require-record", data[p0].id)
            editor.apply()

            val intent = Intent(context, WorkoutActivity::class.java)
            context.startActivity(intent)
        }

        return generatedView
    }
}



class UserActivity : AppCompatActivity() {
    private lateinit var adapter: UserAdapter
    private val userList = ArrayList<User>()

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user)
        val listView = findViewById<ListView>(R.id.listview)

        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val loginedUser = sharedPreferences.getString("id", null)

        adapter = UserAdapter(this, userList, loginedUser)
        listView.adapter = adapter

        fetchUserData()
    }

    private fun fetchUserData() {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("user-info")
        val workoutCollection = db.collection("workout-record")

        userCollection.get().addOnSuccessListener { querySnapshot ->
            for (doc in querySnapshot.documents) {
                val id = doc.getString("id") ?: continue
                val greet = doc.getString("greet") ?: ""
                val name = doc.getString("name") ?: ""
                val password = doc.getString("password") ?: continue
                val user = User(id, password, greet, 0, name)

                workoutCollection.document(id).get().addOnSuccessListener { workoutDoc ->
                    if (workoutDoc.exists()) {
                        val detailsMap = workoutDoc.data?.get("details") as? Map<*, *> ?: emptyMap<Any, Any>()
                        user.workcnt = detailsMap.size
                    }
                    userList.add(user)
                    adapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
