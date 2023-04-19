package com.example.cranny

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*

class AddBookPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book_page)

        val TitleInput=findViewById<EditText>(R.id.etTitleInput)
        val AuthorInput=findViewById<EditText>(R.id.etAuthorInput)
        val SummaryInput=findViewById<EditText>(R.id.etSummaryInput)
        val PurchasedFromInput=findViewById<EditText>(R.id.etPurchasedFrom)
        val ReviewInput=findViewById<EditText>(R.id.etReview)
        val LastPageReadInput=findViewById<TextView>(R.id.tvPageRead)
        val DataStartedInput=findViewById<TextView>(R.id.tvDateStarted)
        val FinishedCB=findViewById<CheckBox>(R.id.cbFinished)
        val CancelBTN=findViewById<Button>(R.id.btnCancel)
        val SaveBTN=findViewById<Button>(R.id.btnSave)

    }
}