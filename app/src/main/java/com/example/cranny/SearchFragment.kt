package com.example.cranny

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.cranny.model.BookAdapter
import com.example.cranny.model.BooksViewModel
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONException
import java.util.*
import androidx.lifecycle.Observer

class SearchFragment : Fragment() {

    private lateinit var searchEdt: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        progressBar = view.findViewById(R.id.idLoadingPB)
        searchEdt = view.findViewById(R.id.idEdtSearchBooks)

        // Handle search operation
        searchEdt.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                progressBar.visibility = View.VISIBLE

                if (searchEdt.text.toString().isEmpty()) {
                    searchEdt.error = "Please enter search query"
                } else {
                    getBooksInfo(searchEdt.text.toString())
                }

                // Close virtual keyboard
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun getBooksInfo(query: String) {
        //search function for api book scrape
        // Initialize the book view model
        val bookViewModel = ViewModelProvider(this).get(BooksViewModel::class.java)

        // Observe the search results
        bookViewModel.searchResults.observe(viewLifecycleOwner, Observer { books ->
            progressBar.visibility = View.GONE

            // Check if the books list is empty
            if (books.isEmpty()) {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
                return@Observer
            }

            // below line is used to pass our
            // array list in adapter class.
            val adapter = BookAdapter(books, requireContext())

            // below line is used to add linear layout
            // manager for our recycler view.
            val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val mRecyclerView = view?.findViewById<RecyclerView>(R.id.idRVBooks)

            // in below line we are setting layout manager and
            // adapter to our recycler view.
            mRecyclerView?.layoutManager = linearLayoutManager
            mRecyclerView?.adapter = adapter
        })

        // Start the search operation
        bookViewModel.searchBooks(query)
    }
}
