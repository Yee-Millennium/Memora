package com.cs407.memora

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.cs407.memora.model.Question
import androidx.lifecycle.ViewModelProvider
import com.cs407.memora.viewmodel.QuestionDetailViewModel

class QuestionEditActivity : AppCompatActivity() {

    private lateinit var questionEditText: EditText
    private lateinit var answerEditText: EditText
    private var questionId = 0L
    private lateinit var question: Question
    private val questionDetailViewModel: QuestionDetailViewModel by lazy {
        ViewModelProvider(this).get(QuestionDetailViewModel::class.java)
    }

    companion object {
        const val EXTRA_QUESTION_ID = "com.cs407.memora.studyhelper.question_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_edit)

        questionEditText = findViewById(R.id.question_edit_text)
        answerEditText = findViewById(R.id.answer_edit_text)

        findViewById<FloatingActionButton>(R.id.save_button).setOnClickListener { saveButtonClick() }

        // Get question ID from QuestionActivity
        questionId = intent.getLongExtra(EXTRA_QUESTION_ID, -1L)

        if (questionId == -1L) {
            // Add new question
            question = Question()
            question.subjectId = intent.getLongExtra(QuestionActivity.EXTRA_SUBJECT_ID, 0)

            setTitle(R.string.add_question)
        } else {
            // Display existing question from ViewModel
            questionDetailViewModel.loadQuestion(questionId)
            questionDetailViewModel.questionLiveData.observe(this) { question ->
                this.question = question!!
                updateUI()
            }

            setTitle(R.string.edit_question)
        }
    }

    private fun updateUI() {
        questionEditText.setText(question.text)
        answerEditText.setText(question.answer)
    }

    private fun saveButtonClick() {
        question.text = questionEditText.text.toString()
        question.answer = answerEditText.text.toString()

        // TODO: Save new or existing question
        if (questionId == -1L) {
            questionDetailViewModel.addQuestion(question)
        } else {
            questionDetailViewModel.updateQuestion(question)
        }

        // Send back OK result
        setResult(RESULT_OK)
        finish()
    }
}