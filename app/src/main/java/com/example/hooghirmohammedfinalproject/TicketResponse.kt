package com.example.hooghirmohammedfinalproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TicketResponse(private val context: Context, private val events: ArrayList<Event>) : RecyclerView.Adapter<TicketResponse.MyViewHolder>() {

    // Provide a reference to the views for each data item
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ticketImage: ImageView = itemView.findViewById(R.id.imageViewTicket)
        val ticketEvent: TextView = itemView.findViewById(R.id.textViewEvent)
        val ticketVenue: TextView = itemView.findViewById(R.id.textViewVenue)
        val ticketAddress: TextView = itemView.findViewById(R.id.textViewAddress)
        val ticketDate: TextView = itemView.findViewById(R.id.textViewDate)
        val priceRange: TextView = itemView.findViewById(R.id.textViewPriceRange)
        val ticketButton: Button = itemView.findViewById(R.id.buttonVenueLink)

        // See ticket button link
        init {
            ticketButton.setOnClickListener {
                val index = adapterPosition
                if (index != RecyclerView.NO_POSITION) {
                    val currentTicketLink = events[index]
                    val url = currentTicketLink.ticketLink
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = events[position]

        val dateFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY")
        val timeFormat = DateTimeFormatter.ofPattern("h:mm a")

        val highestQualityImage = currentItem.images.maxByOrNull { it.imageUrl.length }
        Glide.with(holder.itemView.context).load(highestQualityImage?.imageUrl).into(holder.ticketImage)

        holder.ticketEvent.text = currentItem.eventName

        val venue = currentItem.venuesEmbedded.venues.firstOrNull()
        holder.ticketVenue.text = "${venue?.venueName ?: "Venue details not available"}, ${venue?.city?.cityName ?: "City not available"}"

        holder.ticketAddress.text = "Address: ${venue?.address?.line1 ?: ""}, ${venue?.city?.cityName ?: "City not available"}, ${venue?.state?.stateName ?: "State not available"}"

        holder.ticketDate.text = "Date: ${LocalDate.parse(currentItem.dates.start.localDate).format(dateFormat)} @ ${LocalTime.parse(currentItem.dates.start.localTime).format(timeFormat)}"

        currentItem.priceRanges?.firstOrNull()?.let {
            holder.priceRange.text = "Price Range: ${it.min.formatAsCurrency()} - ${it.max.formatAsCurrency()}"
            holder.priceRange.visibility = View.VISIBLE
        } ?: run {
            holder.priceRange.visibility = View.GONE
        }
    }

    private fun Double?.formatAsCurrency(): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance()
        return this?.let { currencyFormatter.format(it) } ?: "N/A"
    }
}
