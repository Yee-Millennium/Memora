package com.zybooks.studyhelper.repo;

import androidx.room.*;
import com.zybooks.studyhelper.model.Subject;
import java.util.List;

@Dao
public interface SubjectDao {
    @Query("SELECT * FROM Subject WHERE id = :id")
    Subject getSubject(long id);

    @Query("SELECT * FROM Subject ORDER BY text COLLATE NOCASE")
    List<Subject> getSubjects();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addSubject(Subject subject);

    @Update
    void updateSubject(Subject subject);

    @Delete
    void deleteSubject(Subject subject);
}
