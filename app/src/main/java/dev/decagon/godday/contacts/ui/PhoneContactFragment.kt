package dev.decagon.godday.contacts.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.decagon.godday.contacts.adapter.PhoneContactAdapter
import dev.decagon.godday.contacts.databinding.FragmentPhoneContactBinding



/**
 * This page display the list of contacts on the user's phone if given the necessary permission.
 */
class PhoneContactFragment : Fragment() {

    private var _binding: FragmentPhoneContactBinding? = null
    private val binding get() = _binding!!
    private val phoneContact = mutableListOf<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var message: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhoneContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Checks if the permission to read the phone's contact has been granted to the app. If the
    // permission has been granted, read the contact else, request for the appropriate permission.

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                1
            )
        } else {
            handlePermission()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

       addButton = binding.fab
       message = binding.message

        // This button is use to request for the permission to read contact if it was once denied
        addButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    1
                )
            }
        }
    }

    // This is used to read phone contacts and save them in a mutableList
    private fun getPhoneContact() {
        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)

        while (cursor!!.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            phoneContact.add("$name\n$number")
        }

        cursor.close()
    }

    // If the permission was granted, call the handlePermission() to perform the necessary actions
    // else, display an error message on the screen.

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handlePermission()
            } else {
                message.visibility = View.VISIBLE
            }
        }
    }

    // Handles the actions to be performed when the permission to read read contact has been granted
    // to the app.

    private fun handlePermission() {
        message.visibility = View.GONE
        getPhoneContact()
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = PhoneContactAdapter(phoneContact)
    }
}