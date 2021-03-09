package dev.decagon.godday.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.decagon.godday.contacts.R

/**
 *  Recycler view adapter class which use the data from the phone's contact
 *  to populate the UI of the PhoneContactFragment.
 */

class PhoneContactAdapter(data1: List<String>) : RecyclerView.Adapter<PhoneContactAdapter.ContactViewHolder>() {
    private val data = data1.sorted()
    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.fullName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return ContactViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item
    }

    override fun getItemCount(): Int = data.size


}
