package com.example.kidsapp

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kidsapp.R.raw.wrong_sound

class TasksActivity : AppCompatActivity() {

    private lateinit var animalImage: ImageView
    private lateinit var questionText: TextView
    private lateinit var feedbackText: TextView
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button

    private lateinit var sharedPrefs: SharedPreferences
    private var currentTaskIndex = 0
    private lateinit var currentTask: AnimalTask

    // أصوات للاستخدام
    private lateinit var correctSound: MediaPlayer
    private lateinit var wrongSound: MediaPlayer
    private lateinit var applauseSound: MediaPlayer
    private lateinit var animalSound: MediaPlayer

    private val tasks = listOf(
        AnimalTask(
            R.drawable.lion,
            R.raw.lion_sound,
            "ما اسم الحيوان الذي في الصورة؟",
            "أسد",
            listOf("أسد", "نمر", "فهد", "شق")
        ),
        AnimalTask(
            R.drawable.tiger,
            R.raw.tiger_sound,
            "ما اسم الحيوان الذي في الصورة؟",
            "نمر",
            listOf("نمر", "أسد", "زرافة", "فيل")
        ),
        AnimalTask(
            R.drawable.elephant,
            R.raw.elephant_sound,
            "ما اسم الحيوان الذي في الصورة؟",
            "فيل",
            listOf("فيل", "حصان", "بقرة", "جمل")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        animalImage = findViewById(R.id.animalImage)
        questionText = findViewById(R.id.questionText)
        feedbackText = findViewById(R.id.feedbackText)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)

        // تهيئة الأصوات
        correctSound = MediaPlayer.create(this, R.raw.correct_sound)
        wrongSound = MediaPlayer.create(this, wrong_sound)
        applauseSound = MediaPlayer.create(this, R.raw.applause_sound)

        sharedPrefs = getSharedPreferences("TasksProgress", MODE_PRIVATE)
        currentTaskIndex = sharedPrefs.getInt("current_task", 0)

        if (currentTaskIndex >= tasks.size) {
            showCompletionMessage()
            return
        }

        loadTask(currentTaskIndex)

        // إعداد مستمعين للأزرار
        option1.setOnClickListener { checkAnswer(option1) }
        option2.setOnClickListener { checkAnswer(option2) }
        option3.setOnClickListener { checkAnswer(option3) }
        option4.setOnClickListener { checkAnswer(option4) }
    }

    private fun loadTask(index: Int) {
        currentTask = tasks[index]

        // عرض الصورة والسؤال
        animalImage.setImageResource(currentTask.imageRes)
        questionText.text = currentTask.question

        // تعبئة الخيارات
        option1.text = currentTask.options[0]
        option2.text = currentTask.options[1]
        option3.text = currentTask.options[2]
        option4.text = currentTask.options[3]

        // تشغيل صوت الحيوان
        animalSound = MediaPlayer.create(this, currentTask.soundRes)
        animalSound.start()
    }

    private fun checkAnswer(selectedButton: Button) {
        val selectedAnswer = selectedButton.text.toString()

        if (selectedAnswer == currentTask.correctAnswer) {
            // الإجابة صحيحة
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            feedbackText.text = "أحسنت! إجابة صحيحة"

            // تشغيل الأصوات
            correctSound.start()
            applauseSound.start()

            // الانتقال للمهمة التالية بعد تأخير
            selectedButton.postDelayed({
                currentTaskIndex++
                sharedPrefs.edit().putInt("current_task", currentTaskIndex).apply()

                if (currentTaskIndex >= tasks.size) {
                    showCompletionMessage()
                } else {
                    loadTask(currentTaskIndex)
                    resetOptions()
                }
            }, 2000)
        } else {
            // الإجابة خاطئة
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            feedbackText.text = "حاول مرة أخرى"
            wrongSound.start()

            // إعادة تعيين اللون بعد فترة
            selectedButton.postDelayed({
                selectedButton.setBackgroundResource(R.drawable.option_button)
            }, 1000)
        }
    }

    private fun resetOptions() {
        option1.setBackgroundResource(R.drawable.option_button)
        option2.setBackgroundResource(R.drawable.option_button)
        option3.setBackgroundResource(R.drawable.option_button)
        option4.setBackgroundResource(R.drawable.option_button)
        feedbackText.text = ""
    }

    private fun showCompletionMessage() {
        findViewById<View>(R.id.taskContainer).visibility = View.GONE
        findViewById<TextView>(R.id.completionText).visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        correctSound.release()
        wrongSound.release()
        applauseSound.release()
        animalSound.release()
    }
}

data class AnimalTask(
    val imageRes: Int,
    val soundRes: Int,
    val question: String,
    val correctAnswer: String,
    val options: List<String>
)