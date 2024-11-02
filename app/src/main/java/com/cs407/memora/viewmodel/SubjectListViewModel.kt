package com.cs407.memora.viewmodel;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.cs407.memora.model.Subject
import com.cs407.memora.repo.StudyRepository

enum class SubjectSortOrder {
    ALPHABETIC, NEW_FIRST, OLD_FIRST
}

class SubjectListViewModel(application: Application) : AndroidViewModel(application) {

    private val studyRepo = StudyRepository.getInstance(application)

    private val subjectSortOrderLiveData = MutableLiveData<SubjectSortOrder>()

    val subjectListLiveData: LiveData<List<Subject>> =
        subjectSortOrderLiveData.switchMap { sortOrder ->
            when (sortOrder) {
                SubjectSortOrder.OLD_FIRST -> studyRepo.getSubjectsOldestFirst()
                SubjectSortOrder.NEW_FIRST -> studyRepo.getSubjectsNewestFirst()
                else -> studyRepo.getSubjects()
            }
        }

    fun setSortOrder(sortOrder: SubjectSortOrder) {
        subjectSortOrderLiveData.value = sortOrder
    }

    fun addSubject(subject: Subject) = studyRepo.addSubject(subject)

    fun deleteSubject(subject: Subject) = studyRepo.deleteSubject(subject)
}