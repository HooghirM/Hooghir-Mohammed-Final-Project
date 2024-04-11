package com.example.hooghirmohammedfinalproject

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.firestore.auth.User
//import com.google.gson.annotations.SerializedName
//import org.w3c.dom.Text
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var API_KEY = "NpX8VLS2UGJeuzpUeAGf70SfzAJuSP8Z"
    private var BASE_URL = "https://app.ticketmaster.com/discovery/v2/"
    private val TAG = "MainActivity"

    private lateinit var editTextKeyword: EditText
    private lateinit var editTextCity: EditText
    private lateinit var searchButton: Button
    private lateinit var noResults: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userList = ArrayList<Event>()
        val adapter = TicketResponse(userList)

        editTextKeyword = findViewById(R.id.editTextKeyword)
        editTextCity = findViewById(R.id.editTextCity)
        searchButton = findViewById(R.id.searchButton)
        noResults = findViewById<TextView>(R.id.noResults)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.visibility = View.GONE

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val ticketMasterAPI = retrofit.create(TicketMasterApi::class.java)

        editTextCity.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                searchButton.performClick()
                true
            } else {
                false
            }
        }

        // Hiding keyboard when clicking search
        fun View.hideKeyboard() {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }

        searchButton.setOnClickListener {
            val keyword = editTextKeyword.text.toString().trim()
            val city = editTextCity.text.toString().trim()
            noResults.text = ""
            it.hideKeyboard()
            when {
                keyword.isEmpty() && city.isEmpty() -> showAlert(
                    "Search term and city missing",
                    "Search term and city cannot be empty. Please enter both."
                )

                keyword.isEmpty() -> showAlert(
                    "Search term missing",
                    "Search term cannot be empty. Please enter a search term."
                )

                city.isEmpty() -> showAlert(
                    "Location missing",
                    "City cannot be empty. Please enter a city."
                )

                else -> {
                    userList.clear()
                    ticketMasterAPI.searchEvents(API_KEY, keyword, city, ("date,asc"))
                        .enqueue(object : Callback<TicketData> {
                            override fun onResponse(
                                call: Call<TicketData>,
                                response: Response<TicketData>
                            ) {
                                if (response.isSuccessful && response.body() != null) {
                                    val responseBody = response.body()!!
                                    if (responseBody.embedded?.events.isNullOrEmpty()) {
                                        noResults.text = "No results found."
                                        noResults.visibility = View.VISIBLE
                                        recyclerView.visibility = View.INVISIBLE
                                    } else {
                                        val events = responseBody.embedded.events
                                        userList.addAll(events)
                                        adapter.notifyDataSetChanged()
                                        recyclerView.visibility = View.VISIBLE
                                        noResults.visibility = View.GONE
                                    }
                                } else {
                                    noResults.visibility = View.VISIBLE
                                    recyclerView.visibility = View.GONE
                                }
                            }

                            override fun onFailure(call: Call<TicketData>, t: Throwable) {
                                Log.e(TAG, "API call failed", t)
                                noResults.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            }
                        })
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OKAY") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}