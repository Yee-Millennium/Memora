package com.cs407.memora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.cs407.memora.model.Question
import com.cs407.memora.repo.StudyRepository

class QuestionDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val studyRepo = StudyRepository.getInstance(application)

    private val questionIdLiveData = MutableLiveData<Long>()

    val questionLiveData: LiveData<Question?> =
        questionIdLiveData.switchMap { questionId ->
            studyRepo.getQuestion(questionId)
        }

    fun loadQuestion(questionId: Long) {
        questionIdLiveData.value = questionId
    }

    fun addQuestion(question: Question) = studyRepo.addQuestion(question)

    fun updateQuestion(question: Question) = studyRepo.updateQuestion(question)
}