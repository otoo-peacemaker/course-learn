package com.peacemaker.android.courselearn.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.peacemaker.android.courselearn.R
import com.peacemaker.android.courselearn.model.CourseCard

class CourseCardAdapter(private val cards: List<CourseCard>) : RecyclerView.Adapter<CourseCardAdapter.CreditCardViewHolder>() {
    inner class CreditCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardBg: ConstraintLayout = itemView.findViewById(R.id.cardBg)
        val courseImg: ImageView = itemView.findViewById(R.id.courseImg)
        val courseName: TextView = itemView.findViewById(R.id.courseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.course_card_layout, parent, false)
        return CreditCardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreditCardViewHolder, position: Int) {
        val card = cards[position]
        holder.cardBg.setBackgroundColor(card.bgColor)
        holder.courseImg.setImageDrawable(card.img)
        holder.courseName.text = card.courseName
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}
