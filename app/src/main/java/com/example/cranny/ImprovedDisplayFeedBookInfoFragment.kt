package com.example.cranny

import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


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

    private fun formatStringLength(title: String, formatLength: Int, numLinesAllowed: Int, isIgnoreLineLimit: Boolean): String {
        val words = title.split(" ")
        val formattedLines = mutableListOf<String>()

        var currentLine = StringBuilder()
        var linesAdded = 0

        for (word in words) {
            val wordLengthWithSpace = if (currentLine.isEmpty()) word.length else word.length + 1

            if (currentLine.isNotEmpty() && currentLine.length + wordLengthWithSpace > formatLength) {
                formattedLines.add(currentLine.toString())
                linesAdded++

                if (!isIgnoreLineLimit && linesAdded == numLinesAllowed) {
                    break
                }

                currentLine = StringBuilder()
            }

            currentLine.append(word)
            currentLine.append(' ')
        }

        if (currentLine.isNotEmpty()) {
            formattedLines.add(currentLine.toString().trim())
        }

        return formattedLines.joinToString("\n")
    }
    fun getNumberOfLines(input: String): Int {
        val lineCount = input.split("\n").size
        return lineCount
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
        var ivBookCover: ImageView = fragmentView.findViewById(R.id.ivBook)
        var tvProgressHeader: TextView = fragmentView.findViewById(R.id.tvProgressHeader)
        var tvProgressPages: TextView = fragmentView.findViewById(R.id.tvProgressPages)
        var tvProgressChapters: TextView = fragmentView.findViewById(R.id.tvProgressChapters)
        var pbProgress: ProgressBar = fragmentView.findViewById(R.id.pbProgress)
        var rvBookInsight: RecyclerView = fragmentView.findViewById(R.id.rvBookInsight)
        var rbRating: RatingBar = fragmentView.findViewById(R.id.rbRating)

        // Load book cover
        Glide.with(this)
            .load(book?.strCoverURL)
            .error(R.drawable.logo_mid_green)
            .into(ivBookCover)

        val context = requireContext()
        var progressBarColor: Int
        if(book!!.nTotalReadPages == book.nCountPage)
        {
            progressBarColor = ContextCompat.getColor(context, R.color.cranny_dark_red)
        }
        else
        {
            progressBarColor = ContextCompat.getColor(context, R.color.cranny_purple)
        }
        pbProgress.indeterminateDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)
        pbProgress.progressDrawable.setColorFilter(progressBarColor, PorterDuff.Mode.SRC_IN)

        val progress = (book!!.nTotalReadPages.toFloat() / book.nCountPage.toFloat()) * 100
        pbProgress.progress = progress.toInt()

        setRatingBarColors(rbRating, R.color.cranny_purple, R.color.cBookDisplay_rating_background)



        var title = formatStringLength(book!!.strTitle, 19, 2, false)
        var author = formatStringLength(book!!.strAuthor, 19, 2, false)
        var progressHeader = "@${book.strBookOwnerUsername}'s Progress"
        var pagesRead = "Pages Read:\t${book.nTotalReadPages}/${book.nCountPage}"
        var chaptersRead = "Chapters Read:\t${book.nTotalReadChapters}/${book.nCountChapter}"

        formatTextViewSize(getNumberOfLines(title), tvTitle, 0)
        formatTextViewSize(getNumberOfLines(author), tvAuthor, 1)

        fragmentView.apply {
            tvTitle.text = title
            tvAuthor.text = author
           // rbRating.rating = book?.fRating ?: 0.0f
            tvProgressHeader.text = progressHeader
            tvProgressPages.text = pagesRead
            tvProgressChapters.text = chaptersRead
            rbRating.rating = book.fRating
        }

        var insights = mutableListOf<String>()
        insights.add("Main Characters:\n\n${formatStringLength(book!!.strMainCharacters, 37, 1, true)}")
        insights.add("Genres:\n\n${formatStringLength(book!!.strGenres, 37, 1, true)}")
        insights.add("Tags:\n\n${formatStringLength(book!!.strTags, 37, 1, true)}")
        insights.add("Purchased From:\n\n${formatStringLength(book!!.strPurchasedFrom, 37, 1, true)}")
        insights.add("Summary:\n\n${formatStringLength(book!!.strUserSummary, 37, 1, true)}")
        rvBookInsight.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvBookInsight.setHasFixedSize(true)
        rvBookInsight.adapter = BookFeedDisplayAdapter(insights)

        return fragmentView
    }

    fun setRatingBarColors(ratingBar: RatingBar, filledStarColor: Int, emptyStarColor: Int) {
        val filledStarIntColor = ContextCompat.getColor(ratingBar.context, filledStarColor)
        val emptyStarIntColor = ContextCompat.getColor(ratingBar.context, emptyStarColor)

        val progressDrawable = ratingBar.progressDrawable as LayerDrawable
        val progressIndex = 2
        val secondaryProgressIndex = 1

        val filledStars = ClipDrawable(progressDrawable.getDrawable(progressIndex), Gravity.START, ClipDrawable.HORIZONTAL)
        filledStars.setColorFilter(filledStarIntColor, PorterDuff.Mode.SRC_ATOP)

        val emptyStars = ClipDrawable(progressDrawable.getDrawable(secondaryProgressIndex), Gravity.START, ClipDrawable.HORIZONTAL)
        emptyStars.setColorFilter(emptyStarIntColor, PorterDuff.Mode.SRC_ATOP)

        progressDrawable.setDrawableByLayerId(android.R.id.progress, filledStars)
        progressDrawable.setDrawableByLayerId(android.R.id.secondaryProgress, emptyStars)
    }


    private fun formatTextViewSize(numLines: Int, tv: TextView, tvId: Int)
    {
        var newHeight: Int = 0
        when(tvId)
        {
            0 ->
            {
                // title
                if(numLines == 0)
                {
                    newHeight = resources.getDimensionPixelSize(R.dimen.bookdisplay_title_1line)
                }
                else if(numLines == 1)
                {
                    newHeight = resources.getDimensionPixelSize(R.dimen.bookdisplay_title_2line)
                }
            }
            1 ->
            {
                // author
                if(numLines == 0)
                {
                    newHeight = resources.getDimensionPixelSize(R.dimen.bookdisplay_author_1line)
                }
                else if(numLines == 1)
                {
                    newHeight = resources.getDimensionPixelSize(R.dimen.bookdisplay_author_2line)
                }
            }
        }
        tv.layoutParams.height = newHeight
        tv.requestLayout()
    }
}
class BookFeedDisplayAdapter(private var mlInfo: MutableList<String>
): RecyclerView.Adapter<BookFeedDisplayAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_book_feed_display_layout, parent, false)

        // Create and return a new ViewHolder with the inflated layout
        return MyViewHolder(itemView)
    }
    override fun getItemCount(): Int {
        return mlInfo.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get the username and ID of the current item
        holder.info.text = mlInfo[position]
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize the views in the item view
        val info: TextView = itemView.findViewById(R.id.tvInfo)

    }

}