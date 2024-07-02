package edu.skku.cs.afinal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


data class Workout(
    val day: Int,
    var hasWorkout: Boolean,
    var isToday: Boolean,
    var exercises: List<ExerciseDetail> = emptyList()
) {
    data class ExerciseDetail(
        var exercise: String = "",
        var hour: Int = 0,
        var min: Int = 0,
        var sec: Int = 0,
        var sets: List<Map<String, Any>>? = null
    )
}

class CalendarAdapter(
    private val context: Context,
    private val dateList: ArrayList<Workout>,
    private val today: Int,
    private val onDateClick: (Workout) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {

    var selectedPosition = today + 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calander_date_item, parent, false)
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val itemWidth = screenWidth / 7
        view.layoutParams = ViewGroup.LayoutParams((50.toFloat() * displayMetrics.density).toInt(), (50.toFloat() * displayMetrics.density).toInt()) // Set item width and fixed height

        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val workout = dateList[position]
        holder.tvDate.text = if (workout.day == 0) "" else workout.day.toString()

        if (workout.isToday) {
            holder.itemView.setBackgroundResource(R.drawable.red_boarder)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        if (workout.hasWorkout) {
            holder.ivWorkout.visibility = View.VISIBLE
        } else {
            holder.ivWorkout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (workout.day > today) {
                Toast.makeText(context, "Can't add records in the future", Toast.LENGTH_SHORT).show()
                selectedPosition = today - 1
            } else {
                selectedPosition = position
                val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("selected_day", workout.day)
                editor.apply()
                onDateClick(workout) // Notify the activity of the clicked date
            }
            notifyDataSetChanged()
        }

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.red_boarder)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivWorkout: ImageView = itemView.findViewById(R.id.ivWorkout)
    }
}

class ExerciseDetailAdapter(
    context: Context,
    private val exerciseDetails: List<Workout.ExerciseDetail>
) : ArrayAdapter<Workout.ExerciseDetail>(context, 0, exerciseDetails) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.exercise_detail, parent, false)
        }

        val exerciseDetail = getItem(position)

        val imgView = view!!.findViewById<ImageView>(R.id.imageView)

        val container = view.findViewById<LinearLayout>(R.id.textContainer)
        val exerciseTextView = view.findViewById<TextView>(R.id.textViewTitle)
        exerciseTextView.text = exerciseDetail?.exercise ?: ""
        if(exerciseDetail?.exercise == "Running") imgView.setImageResource(R.drawable.running)
        else if (exerciseDetail?.exercise == "Cycling") imgView.setImageResource(R.drawable.cycling)
        else if (exerciseDetail?.exercise == "Yoga") imgView.setImageResource(R.drawable.yoga)
        else if (exerciseDetail?.exercise == "Deadlift") imgView.setImageResource(R.drawable.deadlife)
        else if (exerciseDetail?.exercise == "Bench Press") imgView.setImageResource(R.drawable.bench)
        else if (exerciseDetail?.exercise == "Dumbbell Lunge") imgView.setImageResource(R.drawable.dumbbelllunge)
        else if (exerciseDetail?.exercise == "Front Squat") imgView.setImageResource(R.drawable.frontsquat)
        else if (exerciseDetail?.exercise == "Pull Up") imgView.setImageResource(R.drawable.pollup)
        else if (exerciseDetail?.exercise == "Push Up") imgView.setImageResource(R.drawable.pushup)
        else if (exerciseDetail?.exercise == "Incline Dumbbell Curl") imgView.setImageResource(R.drawable.incline)
        else if (exerciseDetail?.exercise == "Standing Dumbbell Curl") imgView.setImageResource(R.drawable.standingdumbbellcurl)

        val set1TextView = view.findViewById<TextView>(R.id.set1)
        val set2TextView = view.findViewById<TextView>(R.id.set2)
        val set3TextView = view.findViewById<TextView>(R.id.set3)
        val set4TextView = view.findViewById<TextView>(R.id.set4)

        if (exerciseDetail?.sets != null) {
            exerciseDetail?.sets?.let { sets ->
                if (sets.size >= 1) {
                    set1TextView.text = "${sets[0]["kg"]}kg X ${sets[0]["rep"]} reps"
                }
                if (sets.size >= 2) {
                    set2TextView.text = "${sets[1]["kg"]}kg X ${sets[1]["rep"]} reps"
                }
                if (sets.size >= 3) {
                    set3TextView.text = "${sets[2]["kg"]}kg X ${sets[2]["rep"]} reps"
                }
                if (sets.size >= 4) {
                    set4TextView.text = "${sets[3]["kg"]}kg X ${sets[3]["rep"]} reps"
                }
            }
        } else {
            container.visibility = View.GONE
            set1TextView.text = ""
            set2TextView.text = ""
            set3TextView.text = ""
            set4TextView.text = ""
        }

        return view
    }
}

class WorkoutActivity : AppCompatActivity() {

    private lateinit var recyclerViewCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private val dateList = ArrayList<Workout>()
    private val db = FirebaseFirestore.getInstance()
    private val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var firstDayOfWeek: Int = 0

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar)
        recyclerViewCalendar.layoutManager = GridLayoutManager(this, 7)

        generateCalendar()
        loadWorkoutData()

        val addBtn = findViewById<Button>(R.id.btnAddRecord)
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val requireRecord = sharedPreferences.getString("require-record", "")
        val id = sharedPreferences.getString("id", "")
        val editor = sharedPreferences.edit()
        editor.putInt("selected_day", today)
        editor.apply()

        if (requireRecord != id) {
            addBtn.visibility = View.GONE
        } else {
            addBtn.visibility = View.VISIBLE
        }

        addBtn.setOnClickListener {
            val intent: Intent = Intent(
                this@WorkoutActivity,
                AddExerciseActivity::class.java
            )
            startActivity(intent)
        }
    }

    private fun generateCalendar() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        for (i in 1 until firstDayOfWeek) {
            dateList.add(Workout(day = 0, hasWorkout = false, isToday = false))
        }

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDay) {
            dateList.add(Workout(day = i, hasWorkout = false, isToday = i == today))
        }

        calendarAdapter = CalendarAdapter(this, dateList, today) { workout ->
            updateWorkoutDetails(workout)
        }
        recyclerViewCalendar.adapter = calendarAdapter
    }

    private fun loadWorkoutData() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val requiredRecordId = sharedPreferences.getString("require-record", null) ?: return

        db.collection("workout-record").document(requiredRecordId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val details = document.get("details") as? Map<String, List<Map<String, Any>>> ?: return@addOnSuccessListener
                    for ((dayString, exercises) in details) {
                        val day = dayString.toIntOrNull() ?: continue
                        val actualDay = day + firstDayOfWeek - 1 // 실제 날짜 위치 계산
                        if (actualDay in 1..dateList.size) {
                            val exerciseDetails = exercises.map { exerciseDetail ->
                                Workout.ExerciseDetail(
                                    exercise = exerciseDetail["exercise"] as? String ?: "",
                                    hour = (exerciseDetail["hour"] as? Long)?.toInt() ?: 0,
                                    min = (exerciseDetail["min"] as? Long)?.toInt() ?: 0,
                                    sec = (exerciseDetail["sec"] as? Long)?.toInt() ?: 0,
                                    sets = exerciseDetail["sets"] as? List<Map<String, Any>>
                                )
                            }

                            dateList[actualDay - 1] = Workout(
                                day = day,
                                hasWorkout = true,
                                isToday = day == today,
                                exercises = exerciseDetails
                            )

                            // Log the retrieved data for verification
                            Log.d("WorkoutData", "Day: $day, Exercises: $exerciseDetails")
                        }
                    }
                    val todayWorkout = dateList.find { it.isToday }
                    if (todayWorkout != null) {
                        updateWorkoutDetails(todayWorkout)
                    }
                }
                calendarAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load workout data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateWorkoutDetails(workout: Workout) {
        val date = findViewById<TextView>(R.id.textView37)
        val counts = findViewById<TextView>(R.id.textView39)
        val showEx = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val listView = findViewById<ListView>(R.id.showlist)

        date.text = "6.${workout.day}. "
        val workoutCount = workout.exercises.size
        val totalMinutes = workout.exercises.sumOf { it.hour * 60 + it.min }

        val adapter = ExerciseDetailAdapter(this, workout.exercises)
        listView.adapter = adapter

        if (workout.hasWorkout) {
            counts.text = "\uD83D\uDCAA $workoutCount Exercises ⏱️ $totalMinutes minutes"
            showEx.visibility = View.VISIBLE
        } else {
            showEx.visibility = View.GONE
        }
    }
}