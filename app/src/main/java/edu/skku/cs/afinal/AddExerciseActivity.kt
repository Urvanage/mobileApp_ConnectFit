package edu.skku.cs.afinal


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.firebase.firestore.FirebaseFirestore
import edu.skku.cs.afinal.R

class AddExerciseActivity : AppCompatActivity() {
    private lateinit var exerciseSpinner: Spinner
    private lateinit var exerciseImage: ImageView
    private lateinit var exerciseName: TextView
    private lateinit var exerciseDescription: TextView
    private lateinit var setsEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var setsEditText2: EditText
    private lateinit var repsEditText2: EditText
    private lateinit var setsEditText3: EditText
    private lateinit var repsEditText3: EditText
    private lateinit var setsEditText4: EditText
    private lateinit var repsEditText4: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var Exercise: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_exercise)
        Exercise = ""

        db = FirebaseFirestore.getInstance()

        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "") ?: ""
        val clickedDate = sharedPreferences.getInt("selected_day", -1)
        //Toast.makeText(this, "Clicked Date: $clickedDate", Toast.LENGTH_SHORT).show()

        exerciseSpinner = findViewById(R.id.exerciseSpinner)
        exerciseImage = findViewById(R.id.exerciseImage)
        exerciseName = findViewById(R.id.exerciseDescriptionTitle)
        exerciseDescription = findViewById(R.id.exerciseDescription)
        setsEditText = findViewById(R.id.setsEditText)
        repsEditText = findViewById(R.id.repsEditText)
        setsEditText2 = findViewById(R.id.setsEditText2)
        repsEditText2 = findViewById(R.id.repsEditText2)
        setsEditText3 = findViewById(R.id.setsEditText3)
        repsEditText3 = findViewById(R.id.repsEditText3)
        setsEditText4 = findViewById(R.id.setsEditText4)
        repsEditText4 = findViewById(R.id.repsEditText4)

        val fit = findViewById<Button>(R.id.button6)
        val running = findViewById<Button>(R.id.button7)
        val yoga = findViewById<Button>(R.id.button8)
        val cycling = findViewById<Button>(R.id.button9)

        fit.setOnClickListener {
            showExerciseDetails()
            Exercise = "fit"
        }
        running.setOnClickListener {
            hideExerciseDetails()
            Exercise = "Running"
        }
        yoga.setOnClickListener {
            hideExerciseDetails()
            Exercise = "Yoga"
        }
        cycling.setOnClickListener {
            hideExerciseDetails()
            Exercise = "Cycling"
        }

        val exerciseAdapter = ArrayAdapter.createFromResource(
            this, R.array.exercise_list, android.R.layout.simple_spinner_item
        )
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        exerciseSpinner.adapter = exerciseAdapter

        exerciseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateExerciseDetails(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val outBtn = findViewById<Button>(R.id.outBtn)
        outBtn.setOnClickListener {
            addExerciseRecord()
        }
    }

    fun showExerciseDetails() {
        val exerciseDetailsLayout = findViewById<LinearLayout>(R.id.exerciseDetailsLayout)
        exerciseDetailsLayout.visibility = View.VISIBLE
    }

    fun hideExerciseDetails(){
        val exerciseDetailsLayout = findViewById<LinearLayout>(R.id.exerciseDetailsLayout)
        exerciseDetailsLayout.visibility = View.GONE
    }

    private fun updateExerciseDetails(position: Int) {
        // 선택된 운동에 따라 이미지, 이름, 설명 업데이트
        when (position) {
            0 -> {  // Deadlift
                Exercise = "Deadlift"
                exerciseImage.setImageResource(R.drawable.deadlife)
                exerciseName.text = "Whole body"
                exerciseDescription.text = "Compound weightlifting exercise that primarily targets the muscles of the lower back, glutes, hamstrings, and core"
            }
            1 -> {  // Dumbbell Lunge
                Exercise = "Dumbbell Lunge"
                exerciseImage.setImageResource(R.drawable.dumbbelllunge)
                exerciseName.text = "Front thigh, hip up"
                exerciseDescription.text = "Most commonly known lower body workout alongside with squats. You can target glutes and thigh muscles directly with this workout. It is also excellent for developing coordination of lower body muscles!"
            }
            2 -> {
                Exercise = "Front Squat"
                exerciseImage.setImageResource(R.drawable.frontsquat)
                exerciseName.text = "Front thigh"
                exerciseDescription.text = "It is a workout that can give strong target to the muscles in front of your thighs with the barbell on your front shoulder!"
            }
            3 -> {
                Exercise = "Incline Dumbbell Curl"
                exerciseImage.setImageResource(R.drawable.incline)
                exerciseName.text = "Toned arms"
                exerciseDescription.text = "If you lie down on an incline bench, the range of arm movement is longer, so the degree of muscle intervention increases. It's an effective workout to increase the length of your biceps!"
            }
            4 -> {
                Exercise = "Bench Press"
                exerciseImage.setImageResource(R.drawable.bench)
                exerciseName.text = "Overall chest"
                exerciseDescription.text = "This is one of the most common chest workouts. Even if you only do bench presses for your pecs, it may suffice."
            }
            5 -> {
                Exercise = "Pull Up"
                exerciseImage.setImageResource(R.drawable.pollup)
                exerciseName.text = "Wide shoulders, back"
                exerciseDescription.text = "You pull your bodywieght up with your upper body strength in this exercise. It is called pull-up when your palms face outwards, and chin-up when your palms face inwards."
            }
            6 -> {
                Exercise = "Push Up"
                exerciseImage.setImageResource(R.drawable.pushup)
                exerciseName.text = "Overall chest"
                exerciseDescription.text = "It's a most famous, and at the same time highly effective, body weight exercise. If you put your hands further apart, you can target the outside of your pecs."
            }
            7 -> {
                Exercise = "Standing Dumbbell Curl"
                exerciseImage.setImageResource(R.drawable.standingdumbbellcurl)
                exerciseName.text = "Biceps"
                exerciseDescription.text = "An isolation exercise that primarily targets the muscles of the biceps."
            }
            else -> {
                // 기본적으로 아무 작업도 하지 않음
            }
        }

        // 세트와 반복 수 입력란 초기화
        setsEditText.setText("")
        repsEditText.setText("")
        setsEditText2.setText("")
        repsEditText2.setText("")
        setsEditText3.setText("")
        repsEditText3.setText("")
        setsEditText4.setText("")
        repsEditText4.setText("")
    }

    private fun addExerciseRecord() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("id", "") ?: ""
        val clickedDate = sharedPreferences.getInt("selected_day", -1)
        val exerciseName = Exercise
        val hour = findViewById<EditText>(R.id.editTextNumber3).text.toString().toIntOrNull() ?: 0
        val min = findViewById<EditText>(R.id.editTextNumber2).text.toString().toIntOrNull() ?: 0
        val sec = findViewById<EditText>(R.id.editTextNumber).text.toString().toIntOrNull() ?: 0

        if (hour == 0 && min == 0 && sec == 0) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
            return
        } else if (min >= 60 || sec >= 60) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
            return
        }
        if (Exercise == ""){
            Toast.makeText(this, "Please select an exercise", Toast.LENGTH_SHORT).show()
            return
        }

        val docRef = FirebaseFirestore.getInstance().collection("workout-record").document(userId)

        var detailRecordCheck = true

        if(Exercise == "Cycling" || Exercise=="Yoga" || Exercise == "Running") detailRecordCheck = false

        docRef.get().addOnSuccessListener { document ->
            val detailsMap = document.data?.get("details") as? MutableMap<String, Any> ?: mutableMapOf()
            val dateList = detailsMap[clickedDate.toString()] as? MutableList<Map<String, Any>> ?: mutableListOf()

            if (detailRecordCheck) {
                val setsEditText = findViewById<EditText>(R.id.setsEditText)
                val setsEditText2 = findViewById<EditText>(R.id.setsEditText2)
                val setsEditText3 = findViewById<EditText>(R.id.setsEditText3)
                val setsEditText4 = findViewById<EditText>(R.id.setsEditText4)
                val repsEditText = findViewById<EditText>(R.id.repsEditText)
                val repsEditText2 = findViewById<EditText>(R.id.repsEditText2)
                val repsEditText3 = findViewById<EditText>(R.id.repsEditText3)
                val repsEditText4 = findViewById<EditText>(R.id.repsEditText4)

                val setValues = mutableListOf<Map<String, Int>>()
                val setKg = listOf(
                    setsEditText.text.toString().toIntOrNull() ?: 0,
                    setsEditText2.text.toString().toIntOrNull() ?: 0,
                    setsEditText3.text.toString().toIntOrNull() ?: 0,
                    setsEditText4.text.toString().toIntOrNull() ?: 0
                )
                val setRep = listOf(
                    repsEditText.text.toString().toIntOrNull() ?: 0,
                    repsEditText2.text.toString().toIntOrNull() ?: 0,
                    repsEditText3.text.toString().toIntOrNull() ?: 0,
                    repsEditText4.text.toString().toIntOrNull() ?: 0
                )

                for (i in 0 until 4) {
                    setValues.add(mapOf("kg" to setKg[i], "rep" to setRep[i]))
                }

                if (setValues.any { it["kg"] == 0 || it["rep"] == 0 }) {
                    Toast.makeText(this, "Please input valid sets and repetitions for the performed exercise.", Toast.LENGTH_SHORT).show()
                } else {
                    val newDetailsObject = mapOf(
                        "exercise" to exerciseName,
                        "hour" to hour,
                        "min" to min,
                        "sec" to sec,
                        "sets" to setValues
                    )

                    val existingExerciseIndex = dateList.indexOfFirst { it["exercise"] == exerciseName }
                    if (existingExerciseIndex != -1) {
                        dateList[existingExerciseIndex] = newDetailsObject
                    } else {
                        dateList.add(newDetailsObject)
                    }

                    detailsMap[clickedDate.toString()] = dateList
                    docRef.update("details", detailsMap).addOnSuccessListener {
                        Toast.makeText(this, "Record added successfully!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, WorkoutActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error updating document: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val newDetailsObject = mapOf(
                    "exercise" to exerciseName,
                    "hour" to hour,
                    "min" to min,
                    "sec" to sec
                )

                val existingExerciseIndex = dateList.indexOfFirst { it["exercise"] == exerciseName }
                if (existingExerciseIndex != -1) {
                    dateList[existingExerciseIndex] = dateList[existingExerciseIndex].toMutableMap().apply {
                        put("hour", (this["hour"] as Int) + hour)
                        put("min", (this["min"] as Int) + min)
                        put("sec", (this["sec"] as Int) + sec)
                    }
                } else {
                    dateList.add(newDetailsObject)
                }

                detailsMap[clickedDate.toString()] = dateList
                docRef.update("details", detailsMap).addOnSuccessListener {
                    Toast.makeText(this, "Record added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, WorkoutActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error updating document: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error retrieving document: ${it.message}", Toast.LENGTH_SHORT).show()
        }

    }
}