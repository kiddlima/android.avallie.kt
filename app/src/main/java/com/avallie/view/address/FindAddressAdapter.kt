package com.avallie.view.address

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avallie.R
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.android.synthetic.main.prediction_layout.view.*

typealias OnPredictionClicked = (prediction: AutocompletePrediction) -> Unit

class FindAddressAdapter(
    val context: Context,
    private val predictions: MutableList<AutocompletePrediction>,
    val onPredictionClicked: OnPredictionClicked
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PredictionViewHolder(
            LayoutInflater.from(context).inflate(R.layout.prediction_layout, parent, false)
        )
    }

    override fun getItemCount(): Int = predictions.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as PredictionViewHolder

        holder.firstText.text = predictions[position].getPrimaryText(null)
        holder.secondText.text = predictions[position].getSecondaryText(null)
    }

    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var firstText: TextView = itemView.first_text
        var secondText: TextView = itemView.second_text
        
        init {
            itemView.setOnClickListener {
                onPredictionClicked.invoke(predictions[adapterPosition])
            }
        }
    }
}