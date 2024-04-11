package com.example.hooghirmohammedfinalproject

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
import org.w3c.dom.Text
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TicketResponse(private val users: ArrayList<Event>) : RecyclerView.Adapter<TicketResponse.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 3 view items in our row_item.xml file
        val ticketImage = itemView.findViewById<ImageView>(R.id.imageViewTicket)
        val ticketEvent = itemView.findViewById<TextView>(R.id.textViewEvent)
        val ticketVenue = itemView.findViewById<TextView>(R.id.textViewVenue)
        val ticketAddress = itemView.findViewById<TextView>(R.id.textViewAddress)
        val ticketDate = itemView.findViewById<TextView>(R.id.textViewDate)
        val priceRange = itemView.findViewById<TextView>(R.id.textViewPriceRange)
        val ticketButton = itemView.findViewById<Button>(R.id.buttonVenueLink)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder
        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ticket_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Assuming `events` is the correct list of `Event` objects
        val currentItem = users[position]

        val dateFormat = DateTimeFormatter.ofPattern("MM/dd/YYYY")
        val timeFormat = DateTimeFormatter.ofPattern("h:mm a")

        // Load the event image
        val highestQualityImage = currentItem.images.maxByOrNull { it.imageUrl.length }
        Glide.with(holder.itemView.context)
            .load(highestQualityImage?.imageUrl)
            .into(holder.ticketImage)

        // Set event name
        holder.ticketEvent.text = currentItem.eventName

        // Assuming there's at least one venue in the list and we're taking the first one
        val venue = currentItem.venuesEmbedded.venues.firstOrNull()
        holder.ticketVenue.text = "${venue?.venueName ?: "Venue details not available"}, ${venue?.city?.cityName ?: "City not available"}"

        // Set the address, city, and state details together
        holder.ticketAddress.text =
            "Address: ${venue?.address?.line1 ?: ""}, ${venue?.city?.cityName ?: "City not available"}, ${venue?.state?.stateName ?: "State not available"}"

        // Set date and time details
        holder.ticketDate.text =
            "Date: ${LocalDate.parse(currentItem.dates.start.localDate).format(dateFormat)} @ ${LocalTime.parse(currentItem.dates.start.localTime).format(timeFormat)}"


        // Set price range details
        currentItem.priceRanges?.firstOrNull()?.let {
            holder.priceRange.text = "Price Range: ${it.min?.formatAsCurrency()} - ${it.max?.formatAsCurrency()}"
            holder.priceRange.visibility = View.VISIBLE
        } ?: run {
            holder.priceRange.visibility = View.GONE // Make the TextView invisible
        }

        // Set the "See Tickets" button action
        holder.ticketButton.setOnClickListener {
            val ticketLink = venue?.ticketLink
            if (ticketLink != null) {
                val context = holder.itemView.context
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ticketLink))
                context.startActivity(intent)
            }
        }
    }

    // Helper extension function to format a Double as currency
    fun Double?.formatAsCurrency(): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance()
        return this?.let { currencyFormatter.format(it) } ?: "N/A"
    }
}