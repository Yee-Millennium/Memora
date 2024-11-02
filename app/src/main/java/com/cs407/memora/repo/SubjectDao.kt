package com.cs407.memora.repo

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cs407.memora.model.Subject


@Dao
interface SubjectDao {
    @Query("SELECT * FROM Subject WHERE id = :id")
    fun getSubject(id: Long): LiveData<Subject?>

    @Query("SELECT * FROM Subject ORDER BY updated DESC")
    fun getSubjectsNewestFirst(): LiveData<List<Subject>>

    @Query("SELECT * FROM Subject ORDER BY updated ASC")
    fun getSubjectsOldestFirst(): LiveData<List<Subject>>

    @Query("SELECT * FROM Subject ORDER BY text COLLATE NOCASE")
    fun getSubjects(): LiveData<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSubject(subject: Subject): Long

    @Update
    fun updateSubject(subject: Subject)

    @Delete
    fun deleteSubject(subject: Subject)


}