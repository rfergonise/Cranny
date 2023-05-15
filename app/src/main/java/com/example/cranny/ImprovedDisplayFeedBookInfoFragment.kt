package com.example.cranny

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

class ImprovedDisplayFeedBookInfoFragment : DialogFragment() {

    lateinit var fragmentView: View

    companion object {
        fun newInstance(book: DisplayFeedBookInfo): ImprovedDisplayFeedBookInfoFragment {
            val fragment = ImprovedDisplayFeedBookInfoFragment()
            val args = Bundle()
            args.putParcelable("book", book)
            fragment.arguments = args
            return fragment
        }
    }

    private fun formatStringLength(title: String, formatLength: Int, numLinesAllowed: Int): String {
        if (title.length > formatLength) {
            val lines = title.chunked(formatLength)
            val formattedLines = mutableListOf<String>()

            for ((index, line) in lines.withIndex()) {
                if (index < numLinesAllowed - 1) {
                    formattedLines.add(line)
                } else if (index == numLinesAllowed - 1) {
                    val remainingChars = lines.subList(numLinesAllowed - 1, lines.size).joinToString("")
                    if (remainingChars.length <= 3) {
                        formattedLines.add("$line...")
                    } else {
                        formattedLines.add("$line...")
                        formattedLines.add(remainingChars.substring(0, formatLength - 3) + "...")
                    }
                }
            }

            return formattedLines.joinToString("\n")
        } else {
            return title
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_improved_display_feed_book_info, container, false)
        val book = arguments?.getParcelable<DisplayFeedBookInfo>("book")

        var tvTitle: TextView = fragmentView.findViewById(R.id.tvTitle)
        var tvAuthor: TextView = fragmentView.findViewById(R.id.tvAuthor)
        var ivBook: ImageView = fragmentView.findViewById(R.id.ivBook)
        var tvProgressHeader: TextView = fragmentView.findViewById(R.id.tvProgressHeader)
        var tvProgressPages: TextView = fragmentView.findViewById(R.id.tvProgressPages)
        var tvProgressChapters: TextView = fragmentView.findViewById(R.id.tvProgressChapters)
        var pbProgress: ProgressBar = fragmentView.findViewById(R.id.pbProgress)
        var tvMainCharacters: TextView = fragmentView.findViewById(R.id.tvMainCharacters)
        var tvGenres: TextView = fragmentView.findViewById(R.id.tvGenres)
        var tvTags: TextView = fragmentView.findViewById(R.id.tvTags)
        var tvPurchaseFrom: TextView = fragmentView.findViewById(R.id.tvPurchaseFrom)
        var rbRating: RatingBar = fragmentView.findViewById(R.id.rbRating)

        val progressBarColor = ContextCompat.getColor(requireContext(), R.color.cranny_blue_light)
        pbProgress.indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)
        pbProgress.progressDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)

        // todo load profile picture into ivBook

        fragmentView.apply {
            tvTitle.text = formatStringLength(book!!.strTitle, 19, 2)
            tvAuthor.text = formatStringLength(book!!.strAuthor, 19, 2)
            tvMainCharacters.text = "Main Characters:\n\t${formatStringLength(book!!.strMainCharacters, 23, 0)}"
            tvGenres.text = "Genres:\n\t${formatStringLength(book!!.strGenres, 23, 0)}"
            tvTags.text = "Tags:\n\t${formatStringLength(book!!.strTags, 23, 0)}"
            tvPurchaseFrom.text = "Purchased From:\n\t${formatStringLength(book!!.strPurchasedFrom, 23, 0)}"
            rbRating.rating = book?.fRating ?: 0.0f
            tvProgressHeader.text = "@${book.strBookOwnerUsername}'s Progress"
            tvProgressPages.text = "Pages Read:\t${book.nTotalReadPages}/${book.nCountPage}"
            tvProgressChapters.text = "Chapters Read:\t${book.nTotalReadChapters}/${book.nCountChapter}"
        }


        return fragmentView
    }
}