package edu.skku.cs.afinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.Serializable

data class Post(
    val id: String,
    val userid: String,
    val title: String,
    val contents: String,
    val comcontents: List<Comment>,
    val date: String
) : Serializable

data class Comment(
    val commentid: String,
    val comment: String
) : Serializable

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val pageCollection = db.collection("post-page")

    suspend fun fetchPosts(): List<Post> {
        val querySnapshot = pageCollection.get().await()
        val dataArr = mutableListOf<Post>()

        for (doc in querySnapshot.documents) {
            val data = doc.data ?: continue
            val comcontentsList = data["comcontents"] as? List<Map<String, Any>> ?: listOf()

            // 디버깅 메시지 추가
            Log.d("FirestoreRepository", "Document ID: ${doc.id}")
            Log.d("FirestoreRepository", "comcontentsList: $comcontentsList")

            val comcontents = comcontentsList.map { commentMap ->
                Comment(
                    commentid = commentMap["commentid"] as? String ?: "",
                    comment = commentMap["comment"] as? String ?: ""
                )
            }

            val post = Post(
                id = doc.id,
                userid = data["userid"] as? String ?: "",
                title = data["title"] as? String ?: "",
                contents = data["contents"] as? String ?: "",
                comcontents = comcontents,
                date = data["date"] as? String ?: ""
            )

            dataArr.add(post)
        }

        // Sort dataArr based on date in descending order
        dataArr.sortByDescending { it.date }

        return dataArr
    }

    suspend fun addCommentToPost(docid: String, comment: Comment) {
        val docRef = pageCollection.document(docid)

        val existingData = docRef.get().await().data
        val existingComments = existingData?.get("comcontents") as? List<Map<String, Any>> ?: emptyList()

        val updatedComments = existingComments.toMutableList()
        updatedComments.add(mapOf("commentid" to comment.commentid, "comment" to comment.comment))

        docRef.update("comcontents", updatedComments)
            .addOnSuccessListener {
                // Successfully updated
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
            .await()
    }

    // Add other functions as needed (e.g., addPost, updatePost, deletePost)
}

class PostAdapter (val context: Context, val data: ArrayList<Post>): BaseAdapter(){
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
        val generatedView = inflater.inflate(R.layout.post,null)

        val idtv = generatedView.findViewById<TextView>(R.id.textView12)
        val titletv = generatedView.findViewById<TextView>(R.id.textView13)
        val commenttv = generatedView.findViewById<TextView>(R.id.textView14)

        idtv.text = data[p0].userid
        titletv.text = data[p0].title
        commenttv.text = data[p0].comcontents.size.toString()

        return generatedView

    }
}

class CommunityActivity : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_community)

        val listview = findViewById<ListView>(R.id.listview)
        val data = ArrayList<Post>()
        val adapter = PostAdapter(this,data)
        listview.adapter = adapter

        val firestoreRepo = FirestoreRepository()
        CoroutineScope(Dispatchers.Main).launch {
            val posts = firestoreRepo.fetchPosts()
            data.addAll(posts)
            listview.adapter = PostAdapter(this@CommunityActivity, posts as ArrayList<Post>)
        }

        listview.setOnItemClickListener { parent, view, position, id ->
            if (data.isNotEmpty()) {
                val selectedPost = data[position]
                val intent = Intent(this, PostDetailActivity::class.java)
                intent.putExtra("selectedPost", selectedPost)
                startActivity(intent)
            }
            else Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show()
        }

        val postbtn = findViewById<TextView>(R.id.button3)
        postbtn.setOnClickListener{
            val intent = Intent(this, AddPostActivity::class.java)
            startActivity(intent)
        }
    }
}