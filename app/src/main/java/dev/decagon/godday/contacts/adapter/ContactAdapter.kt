package dev.decagon.godday.contacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import dev.decagon.godday.contacts.R
import dev.decagon.godday.contacts.model.Contact
import dev.decagon.godday.contacts.ui.ContactListFragmentDirections

/**
 * A recycler view adapter class which use the data from the view model
 * to update and populate the UI of the ContactListFragment
 */

class ContactAdapter(
) :  RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    private var contacts = mutableListOf<Contact>()

    inner class ContactViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
       private lateinit var name: String
       private lateinit var contact: Contact


        /** This method is used by onBindViewHolder to bind the view with data
            It also set click listener for the views, handles navigation and passing of data
           to the ContactDetailsFragment using the Jetpacks navigation component
         */

       fun bind(contact: Contact) {
           this.name = contact.name!!
           this.contact = contact

           val textView = view.findViewById<TextView>(R.id.fullName)
           textView.text = name
           val recyclerView: ViewGroup = view.findViewById(R.id.test)
           recyclerView.setOnClickListener {
               val action = ContactListFragmentDirections.actionContactListFragmentToContactDetailsFragment(contact)
               view.findNavController().navigate(action)
           }
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item, parent, false)
        return ContactViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = contacts[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = contacts.size

    // Method for feeding data into the adapter class

    fun setContact(contacts: List<Contact>) {
        this.contacts = contacts as MutableList<Contact>
        notifyDataSetChanged()
    }
}