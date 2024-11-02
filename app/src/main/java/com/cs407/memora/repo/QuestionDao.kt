package com.cs407.memora.repo

import androidx.room.*
import com.cs407.memora.model.Question
import androidx.lifecycle.LiveData

@Dao
interface QuestionDao {
    @Query("SELECT * FROM Question WHERE id = :id")
    fun getQuestion(id: Long): LiveData<Question?>

    @Query("SELECT * FROM Question WHERE subject_id = :subjectId ORDER BY id")
    fun getQuestions(subjectId: Long): LiveData<List<Question>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuestion(question: Question): Long

    @Update
    fun updateQuestion(question: Question)

    @Delete
    fun deleteQuestion(question: Question)
}