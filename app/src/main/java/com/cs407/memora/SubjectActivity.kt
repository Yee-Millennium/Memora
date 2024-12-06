package com.cs407.memora

// 6.4.20
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.cs407.memora.model.Subject
import com.cs407.memora.viewmodel.SubjectListViewModel
import androidx.lifecycle.ViewModelProvider
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.graphics.Color
import android.widget.Button
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager

enum class SubjectSortOrder {
    ALPHABETIC, NEW_FIRST, OLD_FIRST
}

class SubjectActivity : AppCompatActivity(),
    SubjectDialogFragment.OnSubjectEnteredListener {

    private var subjectAdapter = SubjectAdapter(mutableListOf())
    private lateinit var subjectRecyclerView: RecyclerView
    private lateinit var subjectColors: IntArray
//    private lateinit var subjectListViewModel: SubjectListViewModel
    private val subjectListViewModel: SubjectListViewModel by lazy {
        ViewModelProvider(this).get(SubjectListViewModel::class.java)
    }
    private var loadSubjectList = true
    private lateinit var selectedSubject: Subject
    private var selectedSubjectPosition = RecyclerView.NO_POSITION
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

//        subjectListViewModel = SubjectListViewModel(application)

        subjectColors = resources.getIntArray(R.array.subjectColors)

        findViewById<FloatingActionButton>(R.id.add_subject_button).setOnClickListener {
            addSubjectClick() }

        subjectRecyclerView = findViewById(R.id.subject_recycler_view)
        subjectRecyclerView.layoutManager = LinearLayoutManager(this)
//        subjectRecyclerView.layoutManager = GridLayoutManager(applicationContext,2)
        subjectRecyclerView.adapter = subjectAdapter

        subjectListViewModel.subjectListLiveData.observe(
            this, { subjectList ->
                if (loadSubjectList) {
                    updateUI(subjectList)
                }
            })

//        // Show the subjects
//        updateUI(subjectListViewModel.getSubjects())

    }
    private fun getSettingsSortOrder(): SubjectSortOrder {

        // Set sort order from settings
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val sortOrderPref = sharedPrefs.getString("subject_order", "alpha")
        return when (sortOrderPref) {
            "alpha" -> SubjectSortOrder.ALPHABETIC
            "new_first" -> SubjectSortOrder.NEW_FIRST
            else -> SubjectSortOrder.OLD_FIRST
        }
    }

    private fun updateUI(subjectList: List<Subject>) {
        subjectAdapter = SubjectAdapter(subjectList as MutableList<Subject>)
        subjectAdapter.sortOrder = getSettingsSortOrder()
        subjectRecyclerView.adapter = subjectAdapter
    }

    override fun onSubjectEntered(subjectText: String) {
        if (subjectText.isNotEmpty()) {
            val subject = Subject(0, subjectText)
            // Stop updateUI() from being called
            loadSubjectList = false
            subjectListViewModel.addSubject(subject)
            // Add subject to RecyclerView
            subjectAdapter.addSubject(subject)
            Toast.makeText(this, "Added $subjectText", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun addSubjectClick() {
//        val dialog = SubjectDialogFragment()
//        dialog.show(supportFragmentprivate inner class SubjectAdapter(private val subjectList: MutableList<Subject>) Manager, "subjectDialog")
//    }
    private fun addSubjectClick() {
        val dialog = SubjectDialogFragment()
        dialog.show(supportFragmentManager, "subjectDialog")
    }

//    private inner class SubjectHolder(inflater: LayoutInflater, parent: ViewGroup?) :
//        RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_items, parent, false)),
//        View.OnLongClickListener,
//        View.OnClickListener {
//
//        private var subject: Subject? = null
//        private val subjectTextView: TextView
//
//        init {
//            itemView.setOnClickListener(this)
//            itemView.setOnLongClickListener(this)
//            subjectTextView = itemView.findViewById(R.id.subject_text_view)
//        }
//
//        fun bind(subject: Subject, position: Int) {
//            this.subject = subject
//            subjectTextView.text = subject.text
//            if (selectedSubjectPosition == position) {
//                // Make selected subject stand out
//                subjectTextView.setBackgroundColor(Color.RED)
//            }
//            else {
//               // Make the background color dependent on the length of the subject string
//                val colorIndex = subject.text.length % subjectColors.size
//                subjectTextView.setBackgroundColor(subjectColors[colorIndex])
//            }
//        }
//
//        override fun onClick(view: View) {
//            // Start QuestionActivity with the selected subject
//            val intent = Intent(this@SubjectActivity, QuestionActivity::class.java)
//            intent.putExtra(QuestionActivity.EXTRA_SUBJECT_ID, subject!!.id)
//            intent.putExtra(QuestionActivity.EXTRA_SUBJECT_TEXT, subject!!.text)
//
//            startActivity(intent)
//        }
//
//        override fun onLongClick(view: View): Boolean {
//            if (actionMode != null) {
//                return false
//            }
//
//            selectedSubject = subject!!
//            selectedSubjectPosition = adapterPosition
//
//            // Re-bind the selected item
//            subjectAdapter.notifyItemChanged(selectedSubjectPosition)
//
//            // Show the CAB
//            actionMode = this@SubjectActivity.startActionMode(actionModeCallback)
//            return true
//        }
//    }
private inner class SubjectHolder(inflater: LayoutInflater, parent: ViewGroup?) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.recycler_view_items, parent, false)),
    View.OnLongClickListener,
    View.OnClickListener {

    private var subject: Subject? = null
    private val subjectTextView: TextView
    private val deleteButton: Button = itemView.findViewById(R.id.delete_button)


    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        deleteButton.setOnClickListener {
            subject?.let { subject ->
                subjectAdapter.deleteSubject(subject)
            }
        }
        subjectTextView = itemView.findViewById(R.id.subject_text_view)
    }

    fun bind(subject: Subject, position: Int) {
        this.subject = subject
        subjectTextView.text = subject.text
        subjectTextView.setTextColor(Color.BLACK) // Set text color to black

        // Set background color for the entire card, not the TextView
        val cardView = itemView as androidx.cardview.widget.CardView
        if (selectedSubjectPosition == position) {
            // Make selected subject stand out
            cardView.setCardBackgroundColor(Color.RED)
        } else {
            // Make the background color dependent on the length of the subject string
            val colorIndex = subject.text.length % subjectColors.size
            cardView.setCardBackgroundColor(subjectColors[colorIndex])
        }
    }


    override fun onClick(view: View) {
        // Start QuestionActivity with the selected subject
        val intent = Intent(this@SubjectActivity, QuestionActivity::class.java)
        intent.putExtra(QuestionActivity.EXTRA_SUBJECT_ID, subject!!.id)
        intent.putExtra(QuestionActivity.EXTRA_SUBJECT_TEXT, subject!!.text)
        startActivity(intent)
    }

    override fun onLongClick(view: View): Boolean {
        if (actionMode != null) {
            return false
        }

        selectedSubject = subject!!
        selectedSubjectPosition = adapterPosition

        // Re-bind the selected item
        subjectAdapter.notifyItemChanged(selectedSubjectPosition)

        // Show the CAB
        actionMode = this@SubjectActivity.startActionMode(actionModeCallback)
        return true
    }
}


    private val actionModeCallback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Provide context menu for CAB
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.context_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            if (item.itemId == R.id.delete) {
                // Stop updateUI() from being called
                loadSubjectList = false

                // Delete from ViewModel
                subjectListViewModel.deleteSubject(selectedSubject)

                // Remove from RecyclerView
                subjectAdapter.removeSubject(selectedSubject)

                // Close the CAB
                mode.finish()
                return true
            }

            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null

            // CAB closing, need to deselect item if not deleted
            subjectAdapter.notifyItemChanged(selectedSubjectPosition)
            selectedSubjectPosition = RecyclerView.NO_POSITION
        }


    }



    private inner class SubjectAdapter(private val subjectList: MutableList<Subject>) :
        RecyclerView.Adapter<SubjectHolder>() {

        fun deleteSubject(subject: Subject) {
            val position = subjectList.indexOf(subject)
            if (position >= 0) {
                subjectList.removeAt(position)
                notifyItemRemoved(position)
                // 从 ViewModel 删除数据
                subjectListViewModel.deleteSubject(subject)
            }
        }

        var sortOrder: SubjectSortOrder = SubjectSortOrder.ALPHABETIC
            set(value) {
                when (value) {
                    SubjectSortOrder.OLD_FIRST -> subjectList.sortBy { it.updateTime }
                    SubjectSortOrder.NEW_FIRST -> subjectList.sortByDescending { it.updateTime }
                    else -> subjectList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.text }))
                }
                field = value
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectHolder {
            val layoutInflater = LayoutInflater.from(applicationContext)
            return SubjectHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
            holder.bind(subjectList[position], position)
        }

        override fun getItemCount(): Int {
            return subjectList.size
        }

        fun addSubject(subject: Subject) {

            // Add the new subject at the beginning of the list
            subjectList.add(0, subject)

            // Notify the adapter that item was added to the beginning of the list
            notifyItemInserted(0)

            // Scroll to the top
            subjectRecyclerView.scrollToPosition(0)
        }
        fun removeSubject(subject: Subject) {
            // Find subject in the list
            val index = subjectList.indexOf(subject)
            if (index >= 0) {

                // Remove the subject
                subjectList.removeAt(index)

                // Notify adapter of subject removal
                notifyItemRemoved(index)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.subject_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
//        else if (item.itemId == R.id.import_questions) {
//            intent = Intent(this, ImportActivity::class.java)
//            startActivity(intent)
//            return true
//        }

        return super.onOptionsItemSelected(item)
    }

//    override fun onResume() {
//        super.onResume()
//
//        // Retrieve the sort order from the settings
//        // getSettingsSortOrder() returns a SubjectSortOrder object
//        val sortOrder = getSettingsSortOrder()
//
//        // Set the sort order in the ViewModel
//       subjectListViewModel.setSortOrder(sortOrder)
//    }

    override fun onResume() {
        super.onResume()

        // Retrieve the sort order from the settings
        val sortOrder = getSettingsSortOrder()

        // Map com.cs407.memora.SubjectSortOrder to com.cs407.memora.viewmodel.SubjectSortOrder
        val viewModelSortOrder = when (sortOrder) {
            SubjectSortOrder.ALPHABETIC -> com.cs407.memora.viewmodel.SubjectSortOrder.ALPHABETIC
            SubjectSortOrder.NEW_FIRST -> com.cs407.memora.viewmodel.SubjectSortOrder.NEW_FIRST
            SubjectSortOrder.OLD_FIRST -> com.cs407.memora.viewmodel.SubjectSortOrder.OLD_FIRST
        }

        // Set the sort order in the ViewModel
        subjectListViewModel.setSortOrder(viewModelSortOrder)
    }


}