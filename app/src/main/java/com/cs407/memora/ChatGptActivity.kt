package com.cs407.memora

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.io.IOException
import java.sql.Date

class ChatGptActivity : AppCompatActivity() {
    private lateinit var questionEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatgpt)

        // Initialize views
        questionEditText = findViewById(R.id.question_edit_text)
        sendButton = findViewById(R.id.send_button)
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        loadingProgressBar = findViewById(R.id.loading_progress_bar)

        // Set up RecyclerView
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatGptActivity)
            adapter = chatAdapter
        }

        sendButton.setOnClickListener {
            val question = questionEditText.text.toString().trim()
            if (question.isNotEmpty()) {
                sendQuestion(question)
            }
        }
        // Get the question if it was passed
        intent.getStringExtra("QUESTION")?.let { question ->
            questionEditText.setText(question)
            // Optionally auto-send the question
            sendQuestion(question)
        }
    }

    private fun sendQuestion(question: String) {
        // Add user message to chat
        chatMessages.add(ChatMessage(question, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)

        // Clear input
        questionEditText.text.clear()

        // Show loading indicator
        loadingProgressBar.visibility = View.VISIBLE

        // Make API call to ChatGPT
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ChatGptApi.sendQuestion(question)

                withContext(Dispatchers.Main) {
                    // Add ChatGPT response to chat
                    chatMessages.add(ChatMessage(response, false))
                    chatAdapter.notifyItemInserted(chatMessages.size - 1)
                    loadingProgressBar.visibility = View.GONE

                    // Scroll to bottom
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loadingProgressBar.visibility = View.GONE
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.cancel,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}




