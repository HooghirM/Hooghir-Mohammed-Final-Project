package com.example.hooghirmohammedfinalproject

import com.google.gson.annotations.SerializedName

data class TicketData(
    @SerializedName("_embedded") val embedded: EmbeddedEvents
)

data class EmbeddedEvents(
    @SerializedName("events") val events: List<Event>
)

data class Event(
    @SerializedName("name") val eventName: String,
    @SerializedName("images") val images: List<EventImage>,
    @SerializedName("dates") val dates: EventDate,
    @SerializedName("_embedded") val venuesEmbedded: VenuesEmbedded,
    @SerializedName("priceRanges") val priceRanges: List<PriceRange>?
) {

    data class EventImage(
        @SerializedName("url") val imageUrl: String
    )

    data class EventDate(
        @SerializedName("start") val start: StartDate
    )

    data class StartDate(
        @SerializedName("localDate") val localDate: String,
        @SerializedName("localTime") val localTime: String
    )

    data class VenuesEmbedded(
        @SerializedName("venues") val venues: List<Venue>
    )

    data class Venue(
        @SerializedName("name") val venueName: String,
        @SerializedName("city") val city: VenueCity,
        @SerializedName("state") val state: VenueState,
        @SerializedName("address") val address: VenueAddress,
        @SerializedName("url") val ticketLink: String
    )

    data class VenueCity(
        @SerializedName("name") val cityName: String
    )

    data class VenueState(
        @SerializedName("name") val stateName: String
    )

    data class VenueAddress(
        @SerializedName("line1") val line1: String
    )

    data class PriceRange(
        @SerializedName("min") val min: Double?,
        @SerializedName("max") val max: Double?
    )
}