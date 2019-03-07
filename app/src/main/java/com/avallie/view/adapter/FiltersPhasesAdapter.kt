package com.avallie.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.avallie.model.ConstructionPhase
import kotlinx.android.synthetic.main.phase_item.view.*


typealias ItemClickListener = (phase: ConstructionPhase) -> Unit

class FiltersPhasesAdapter(private val context: Context, private val phases: ArrayList<ConstructionPhase>) :
    RecyclerView.Adapter<FiltersPhasesAdapter.ViewHolder>() {


    var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.phase_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return phases.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val constructionPhase = phases[position]

        holder.phaseName.text = constructionPhase.name

        holder.phaseBackground.setBackgroundResource(
            if (constructionPhase.selected)
                R.drawable.phase_item_selected_background
            else R.drawable.phase_item
        )

        holder.phaseIcon.setImageResource(
            context.resources.getIdentifier(
                "ic_phase_" + constructionPhase.icon,
                "drawable",
                context.packageName
            )
        )

        holder.phaseIcon.setColorFilter(
            if (constructionPhase.selected) Color.WHITE else ContextCompat.getColor(
                context,
                R.color.colorPrimaryLight
            )
        )


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val phaseIcon = itemView.v_phase_icon
        val phaseName = itemView.v_phase_name
        val phaseBackground = itemView.v_phase_background


        init {
            itemView.setOnClickListener {
                itemClickListener?.invoke(phases[adapterPosition])
            }
        }


//        fun onClickListener(listener :(View) -> Unit){
//            itemView.setOnClickListener {
//                listener(it)
//            }
//        }
    }

}