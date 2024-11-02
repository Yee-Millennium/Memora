package com.cs407.memora

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.cs407.memora.model.Subject
import com.cs407.memora.viewmodel.ImportViewModel

class ImportActivity : AppCompatActivity() {

    private lateinit var subjectLayoutContainer: LinearLayout
    private lateinit var loadingProgressBar: ProgressBar

    private val importViewModel: ImportViewModel by lazy {
        ViewModelProvider(this).get(ImportViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        subjectLayoutContainer = findViewById(R.id.subject_layout)

        findViewById<Button>(R.id.import_button).setOnClickListener { importButtonClick() }

        // Show progress bar
        loadingProgressBar = findViewById(R.id.loading_progress_bar)
        loadingProgressBar.visibility = View.VISIBLE

        // Fetch subjects from web API
        importViewModel.fetchSubjects()
        importViewModel.fetchedSubjectList.observe(this, { subjectList: List<Subject> ->

            // Hide progress bar
            loadingProgressBar.visibility = View.GONE

            // Create a checkbox for each subject
            for (subject in subjectList) {
                val checkBox = CheckBox(applicationContext)
                checkBox.textSize = 24f
                checkBox.text = subject.text
                checkBox.tag = subject
                subjectLayoutContainer.addView(checkBox)
            }
        })

        // subjectName changes once all questions are imported
        importViewModel.importedSubject.observe(this, {
            Toast.makeText(applicationContext, "$it imported successfully",
                Toast.LENGTH_SHORT).show()
        })
    }

    private fun importButtonClick() {

        // Add any checked subjects
        for (child in subjectLayoutContainer.children) {
            val checkBox = child as CheckBox
            if (checkBox.isChecked) {
                val subject = checkBox.tag as Subject

                // Fetch this subject's questions
                importViewModel.addSubject(subject)
            }
        }
    }
}