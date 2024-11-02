package com.cs407.memora.repo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cs407.memora.model.Question
import com.cs407.memora.model.Subject

@Database(entities = [Question::class, Subject::class], version = 1)
abstract class StudyDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun subjectDao(): SubjectDao
}