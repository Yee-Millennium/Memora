package com.cs407.memora.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.cs407.memora.model.Subject
import com.cs407.memora.repo.StudyRepository

class ImportViewModel(application: Application) : AndroidViewModel(application) {

    private val studyRepo = StudyRepository.getInstance(application)

    var importedSubject = studyRepo.importedSubject
    var fetchedSubjectList = studyRepo.fetchedSubjectList

    fun addSubject(subject: Subject) {
        studyRepo.addSubject(subject)
        studyRepo.fetchQuestions(subject)
    }

    fun fetchSubjects() = studyRepo.fetchSubjects()
}