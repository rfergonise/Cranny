package com.example.cranny

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cranny.model.BookAdapter
import com.example.cranny.model.BooksViewModel
import com.example.cranny.model.BooksViewModelFactory
import com.example.cranny.model.GoogleBooksRepository
import com.example.cranny.network.googlebooks.RetrofitInstance


class SearchFragment : DialogFragment() {

    private lateinit var searchEdt: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var searchBtn: Button
    private lateinit var bookViewModel: BooksViewModel


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
        searchBtn = view.findViewById(R.id.idBtnSearch)

        // Initialize RecyclerView with an empty adapter
        val mRecyclerView = view.findViewById<RecyclerView>(R.id.idRVBooks)
        mRecyclerView.adapter = BookAdapter(emptyList(), requireContext())

        //Initiate layout manager for RV
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        //Initialize ViewModelProvider.Factory
        val factory = BooksViewModelFactory()

        //Initialize bookViewModel
        bookViewModel = ViewModelProvider(this, factory)[BooksViewModel::class.java]

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

        //click listener for search button
        searchBtn.setOnClickListener {
            val query = searchEdt.text.toString().trim()
            if(query.isEmpty()){
                searchEdt.error = "please enter a book title"
            }
            else{
                progressBar.visibility = View.VISIBLE
                getBooksInfo(query)
            }


        }


    }

    private fun getBooksInfo(query: String) {
        // Observe the search results
        bookViewModel.searchResults.observe(viewLifecycleOwner, Observer { books ->
            progressBar.visibility = View.GONE

            // Check if the books list is empty
            if (books.isEmpty()) {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show()
                return@Observer
            }

            // Update adapter with search results
            val adapter = BookAdapter(books, requireContext())
            val mRecyclerView = view?.findViewById<RecyclerView>(R.id.idRVBooks)
            mRecyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            mRecyclerView?.adapter = adapter
        })

        // Start the search operation
        bookViewModel.searchBooks(query)
    }

}
