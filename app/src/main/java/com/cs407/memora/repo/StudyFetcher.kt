package com.cs407.memora.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cs407.memora.model.Question
import com.cs407.memora.model.Subject
import org.json.JSONException
import org.json.JSONObject

const val TAG = "StudyFetcher"

class StudyFetcher(val context: Context) {

    interface OnStudyDataReceivedListener {
        fun onSubjectsReceived(subjectList: List<Subject>)
        fun onQuestionsReceived(subject: Subject, questionList: List<Question>)
        fun onErrorResponse(error: VolleyError)
    }

    private var requestQueue = Volley.newRequestQueue(context)

    fun fetchSubjects(listener: OnStudyDataReceivedListener) {
//        val url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
//            .appendQueryParameter("type", "subjects").build().toString()
//
//        // Request all subjects
//        val request = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response -> listener.onSubjectsReceived(jsonToSubjects(response)) },
//            { error -> listener.onErrorResponse(error) })
//
//        requestQueue.add(request)
    }

//    private fun jsonToSubjects(json: JSONObject): List<Subject> {
//
//        // Create a list of subjects
//        val subjectList = mutableListOf<Subject>()
//
//        try {
//            val subjectArray = json.getJSONArray("subjects")
//            for (i in 0 until subjectArray.length()) {
//                val subjectObj = subjectArray.getJSONObject(i)
//                val subject = Subject(
//                    text = subjectObj.getString("subject"),
//                    updateTime = subjectObj.getLong("updatetime"))
//                subjectList.add(subject)
//            }
//        } catch (e: JSONException) {
//            Log.e(TAG, "Field missing in the JSON data: ${e.message}")
//        }
//
//        return subjectList
//    }

    fun fetchQuestions(subject: Subject, listener: OnStudyDataReceivedListener) {
//        val url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
//            .appendQueryParameter("type", "questions")
//            .appendQueryParameter("subject", subject.text)
//            .build().toString()
//
//        // Request questions for this subject
//        val jsObjRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response -> listener.onQuestionsReceived(subject, jsonToQuestions(response)) },
//            { error -> listener.onErrorResponse(error) })
//
//        requestQueue.add(jsObjRequest)
    }

//    private fun jsonToQuestions(json: JSONObject): List<Question> {
//
//        // Create a list of questions
//        val questionList = mutableListOf<Question>()
//
//        try {
//            val questionArray = json.getJSONArray("questions")
//            for (i in 0 until questionArray.length()) {
//                val questionObj = questionArray.getJSONObject(i)
//                val question = Question(
//                    text = questionObj.getString("question"),
//                    answer = questionObj.getString("answer"),
//                    subjectId = 0)
//                questionList.add(question)
//            }
//        } catch (e: JSONException) {
//            Log.e(TAG, "Field missing in the JSON data: ${e.message}")
//        }
//
//        return questionList
//    }
}