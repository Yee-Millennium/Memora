package com.cs407.memora
// 6.4.19
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SubjectDialogFragment: DialogFragment() {

    interface OnSubjectEnteredListener {
        fun onSubjectEntered(subjectText: String)
    }

    private lateinit var listener: OnSubjectEnteredListener

override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val view = requireActivity().layoutInflater.inflate(R.layout.dialog_subject, null)
    val subjectEditText = view.findViewById<EditText>(R.id.subject_edit_text)

    return AlertDialog.Builder(requireActivity())
//        .setTitle(R.string.subject)
        .setView(view) // Set the XML layout as the dialog view
        .setPositiveButton(R.string.create) { dialog, whichButton ->
            val subject = subjectEditText.text.toString()
            listener.onSubjectEntered(subject.trim())
        }
        .setNegativeButton(R.string.cancel, null)
        .create()
}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnSubjectEnteredListener
    }
}