package com.cs407.memora.repo

import android.content.Context
import androidx.room.Room
import com.cs407.memora.model.Question
import com.cs407.memora.model.Subject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.VolleyError

class StudyRepository private constructor(context: Context) {
    var importedSubject = MutableLiveData<String>()
    var fetchedSubjectList = MutableLiveData<List<Subject>>()

    private val studyFetcher: StudyFetcher = StudyFetcher(context.applicationContext)

    companion object {
        private var instance: StudyRepository? = null

        fun getSubject(subjectId: Long): LiveData<Subject?> = subjectDao.getSubject(subjectId)

        fun getSubjects(): LiveData<List<Subject>> = subjectDao.getSubjects()

        fun getInstance(context: Context): StudyRepository {
            if (instance == null) {
                instance = StudyRepository(context)
            }
            return instance!!
        }
    }

    private val database : StudyDatabase = Room.databaseBuilder(
        context.applicationContext,
        StudyDatabase::class.java,
        "study.db"
    )
        .allowMainThreadQueries()
        .build()

    private val subjectDao = database.subjectDao()
    private val questionDao = database.questionDao()

    init {
        if (subjectDao.getSubjects().isEmpty()) {
            addStarterData()
        }
    }

    fun fetchSubjects() = studyFetcher.fetchSubjects(fetchListener)

    fun fetchQuestions(subject: Subject) = studyFetcher.fetchQuestions(subject, fetchListener)

    private val fetchListener = object : StudyFetcher.OnStudyDataReceivedListener {
        override fun onSubjectsReceived(subjectList: List<Subject>) {
            fetchedSubjectList.value = subjectList
        }

        override fun onQuestionsReceived(subject: Subject, questionList: List<Question>) {
            for (question in questionList) {
                question.subjectId = subject.id
                addQuestion(question)
            }

            importedSubject.value = subject.text
        }

        override fun onErrorResponse(error: VolleyError) {
            error.printStackTrace()
        }
    }

    fun getSubject(subjectId: Long): Subject? = subjectDao.getSubject(subjectId)

    fun getSubjects(): List<Subject> = subjectDao.getSubjects()

    fun addSubject(subject: Subject) {
        subject.id = subjectDao.addSubject(subject)
    }

    fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    fun getQuestion(questionId: Long): LiveData<Question?> = questionDao.getQuestion(questionId)

    fun getQuestions(subjectId: Long): LiveData<List<Question>> = questionDao.getQuestions(subjectId)

    fun addQuestion(question: Question) {
        question.id = questionDao.addQuestion(question)
    }

    fun updateQuestion(question: Question) = questionDao.updateQuestion(question)

    fun deleteQuestion(question: Question) = questionDao.deleteQuestion(question)

    private fun addStarterData() {
        var subjectId = subjectDao.addSubject(Subject(text = "Math"))
        questionDao.addQuestion(
            Question(
                text = "What is 2 + 3?",
                answer = "2 + 3 = 5",
                subjectId = subjectId
            )
        )
        questionDao.addQuestion(
            Question(
                text = "What is pi?",
                answer = "The ratio of a circle's circumference to its diameter.",
                subjectId = subjectId
            )
        )

        subjectId = subjectDao.addSubject(Subject(text = "History"))
        questionDao.addQuestion(
            Question(
                text = "On what date was the U.S. Declaration of Independence adopted?",
                answer = "July 4, 1776",
                subjectId = subjectId
            )
        )

        subjectDao.addSubject(Subject(text = "Computing"))
    }

    fun getSubjectsNewestFirst(): LiveData<List<Subject>> = subjectDao.getSubjectsNewestFirst()
    fun getSubjectsOldestFirst(): LiveData<List<Subject>> = subjectDao.getSubjectsOldestFirst()
}