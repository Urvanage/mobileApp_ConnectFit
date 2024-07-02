package edu.skku.cs.afinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(val context: Context, var data: ArrayList<Comment>) : BaseAdapter() {
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
        val generatedView = inflater.inflate(R.layout.comment, null)

        val idtv = generatedView.findViewById<TextView>(R.id.textView12)
        val contenttv = generatedView.findViewById<TextView>(R.id.textView15)

        idtv.text = data[p0].commentid
        contenttv.text = data[p0].comment

        return generatedView
    }
}


class PostDetailActivity : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CommunityActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val selectedPost = intent.getSerializableExtra("selectedPost") as Post
        val listview = findViewById<ListView>(R.id.listview)

        val titletv = findViewById<TextView>(R.id.textView16)
        val iddatetv = findViewById<TextView>(R.id.textView17)
        val contenttv = findViewById<TextView>(R.id.textView18)

        titletv.text = selectedPost.title
        iddatetv.text = "${selectedPost.userid} : ${selectedPost.date}"
        contenttv.text = selectedPost.contents

        val commentList = ArrayList(selectedPost.comcontents)

        val adapter = CommentAdapter(this, commentList)
        listview.adapter = adapter

        val uploadCommentButton = findViewById<View>(R.id.button2)
        val enterCommentEditText = findViewById<EditText>(R.id.textView20)

        val db = FirebaseFirestore.getInstance()
        val pageCollection = db.collection("post-page")

        uploadCommentButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("id", null)

            if (userId != null) {
                val commentText = enterCommentEditText.text.toString()
                val commentId = "$userId (${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())})"

                val newComment = Comment(commentId,commentText )
                commentList.add(newComment)
                adapter.notifyDataSetChanged()

                val updatedComments = commentList.map { mapOf("comment" to it.comment, "commentid" to it.commentid) }

                pageCollection.document(selectedPost.id)
                    .update("comcontents", updatedComments)
                    .addOnSuccessListener {
                        /*
                        val intent = Intent(this@PostDetailActivity, PostDetailActivity::class.java)
                        intent.putExtra("selectedPost", selectedPost)
                        finish()
                        startActivity(intent)

                         */
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }

                enterCommentEditText.text.clear()
            } else {
                Toast.makeText(this, "To write a comment, you need to log in first.", Toast.LENGTH_SHORT).show()
                enterCommentEditText.text.clear()
            }
        }

    }
}