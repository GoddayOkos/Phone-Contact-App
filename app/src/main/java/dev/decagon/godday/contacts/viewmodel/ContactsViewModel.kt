package dev.decagon.godday.contacts.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.decagon.godday.contacts.model.Contact
import kotlin.Exception

/**
  A ViewModel class which handles interaction with the database (firebase)
  and update the UI according in Realtime using LiveData. This class contains all
  the methods used in interacting with the database.
**/


class ContactsViewModel : ViewModel() {

    private val dbContacts = FirebaseDatabase.getInstance().getReference("contacts")

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    // To keep track of the transactions
    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    // This method is used to add contacts to the database. It generates a unique key (id)
    // for each contact and save the contact object inside the key path.

    fun addContact(contact: Contact) {
        contact.id = dbContacts.push().key
        dbContacts.child(contact.id!!).setValue(contact)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    // This method is used to retrieve/fetch data from the database in realtime
    // The retrieved data (contact details) is stored in a MutableLiveData

    fun getContact() {
        dbContacts.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val contacts = mutableListOf<Contact>()
                    for (contactSnapshot in snapshot.children) {
                        val contact = contactSnapshot.getValue(Contact::class.java)
                        contact?.id = contactSnapshot.key
                        contact?.let { contacts.add(it) }
                    }
                    _contacts.value = contacts
                }
            }

            // Needed to be overridden/implemented
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    // Edit/Update an existing contact in the database
    // using the unique id of the contact

    fun editContact(contact: Contact) {
        dbContacts.child(contact.id!!).setValue(contact)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }

    // Deletes a contact in the database by setting its value to null
    fun deleteContact(contact: Contact) {
        dbContacts.child(contact.id!!).setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _result.value = null
                } else {
                    _result.value = it.exception
                }
            }
    }
}