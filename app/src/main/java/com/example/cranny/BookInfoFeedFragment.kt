package com.example.cranny

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels


class BookInfoFeedFragment : DialogFragment() {

    companion object {
        fun newInstance(book: SocialFeed): BookInfoFeedFragment {
            val fragment = BookInfoFeedFragment()
            val args = Bundle()
            args.putParcelable("book", book)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_info_feed, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val book = arguments?.getParcelable<SocialFeed>("book")

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        val tvMainCharacters = view.findViewById<TextView>(R.id.tvMainCharacters)
        val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
        val tvTags = view.findViewById<TextView>(R.id.tvTags)
        val tvPurchaseFrom = view.findViewById<TextView>(R.id.tvPurchaseFrom)
        val tvLog = view.findViewById<TextView>(R.id.tvLog)
        val tvStatus = view.findViewById<TextView>(R.id.tvStatus)
        val rbRating = view.findViewById<RatingBar>(R.id.rbRating)
        val ivCover = view.findViewById<ImageView>(R.id.ivCover)
        var pbPagesRead: ProgressBar = view.findViewById(R.id.pbPagesRead)

        // set the color of the progress bar
        val progressBarColor = ContextCompat.getColor(requireContext(), R.color.cranny_purple_blue)
        pbPagesRead.indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)
        pbPagesRead.progressDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)
        //todo pass in the total page count instead of hard coding
        // todo calculate the total pages read from all logs and not just the hardcoded value
        val totalPagesInBook: Int = 400
        val totalPagesRead: Int = 100

        view.apply {
            tvTitle.text = book?.bookTitle
            tvAuthor.text = book?.bookAuthor
            tvMainCharacters.text = "Main Characters:\n" + book?.mainCharacters
            tvGenres.text = "Genres:\n" + book?.genres
            tvTags.text = "Tags:\n" + book?.tags
            tvPurchaseFrom.text = "Purchased From:\n" + book?.purchasedFrom
            tvLog.text = "Journal Entry:\n" + book?.journalEntry
            tvStatus.text = book?.status
            rbRating.rating = book?.starRating ?: 0.0f
            pbPagesRead.max = totalPagesInBook
            pbPagesRead.progress = totalPagesRead
        }

    }
}
