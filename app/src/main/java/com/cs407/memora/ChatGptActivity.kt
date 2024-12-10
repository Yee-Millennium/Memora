package com.cs407.memora

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.snackbar.Snackbar

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

        // Initialize API with any saved key
        ChatGptApi.init(this)

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

        // Check for API key before allowing chat
        if (!hasApiKey()) {
            showApiKeyDialog()
        }

        sendButton.setOnClickListener {
            val question = questionEditText.text.toString().trim()
            if (question.isNotEmpty()) {
                if (hasApiKey()) {
                    sendQuestion(question)
                } else {
                    showApiKeyDialog()
                }
            }
        }

        // Get the question if it was passed
        intent.getStringExtra("QUESTION")?.let { question ->
            questionEditText.setText(question)
            // Only auto-send if we have an API key
            if (hasApiKey()) {
                sendQuestion(question)
            }
        }

        // Enable the back button in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun hasApiKey(): Boolean {
        return getSharedPreferences("app_settings", MODE_PRIVATE)
            .getString("openai_api_key", null) != null
    }

    private fun showApiKeyDialog() {
        val input = EditText(this)
        input.hint = "Enter your OpenAI API key"

        AlertDialog.Builder(this)
            .setTitle("API Key Required")
            .setMessage("Please enter your OpenAI API key to use ChatGPT")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val key = input.text.toString().trim()
                if (key.isNotEmpty()) {
                    ChatGptApi.saveApiKey(this, key)
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "API key saved",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // If they cancel and we have no key, finish the activity
                if (!hasApiKey()) {
                    finish()
                }
            }
            .setCancelable(false) // Prevent dismissing without choosing
            .show()
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
                        when {
                            e.message?.contains("API key not set") == true -> "Please set your API key"
                            else -> getString(R.string.error)
                        },
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Handle the back button click
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}