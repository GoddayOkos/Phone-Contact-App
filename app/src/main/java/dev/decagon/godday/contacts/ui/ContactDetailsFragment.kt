package dev.decagon.godday.contacts.ui

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.decagon.godday.contacts.R
import dev.decagon.godday.contacts.adapter.ContactAdapter
import dev.decagon.godday.contacts.databinding.FragmentContactDetailsBinding
import dev.decagon.godday.contacts.model.Contact
import dev.decagon.godday.contacts.viewmodel.ContactsViewModel


/**
 * This is the contact details page. Here, the user can perform variety of actions like editing details,
 * making calls, sharing contact details, and deleting contacts.
 */

class ContactDetailsFragment : Fragment() {

    private var _binding: FragmentContactDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var contact: Contact
    private lateinit var viewModel: ContactsViewModel
    private lateinit var dialer: ImageView
    private val args by navArgs<ContactDetailsFragmentArgs>()

    // Specify that the fragment has a menu option
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Create the view, initialise the view model and retrieve the contact object that was pass to
    // to this page as argument.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contact = args.contact
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        _binding = FragmentContactDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Setup the details page and populate it with the selected contact's details
    // if the email or phone number field is empty, display a hint of what to do to the user.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        name = binding.fullName.apply { text = contact.name }

        phoneNumber = binding.phoneNumber.apply {
            if (contact.phoneNumber == "") hint = "Add a number" else text = contact.phoneNumber
        }

        email = binding.email.apply {
            if (contact.email == "") hint = "Add an email" else text = contact.email
        }

        dialer = binding.phoneIcon

        /**
         * Once the user click on the dialer, check if the permission to make call has been granted
         * to the app. If the permission is already granted, proceed and make the call. Otherwise,
         * request for the permission.
         */

        dialer.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            } else {
                makeCall()
            }
        }

        // Disable the dialer to prevent users from calling null
        if (phoneNumber.text == "") dialer.isEnabled = false
    }

    // Setting the _binding to null since it's no longer useful at this point
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inflate the menu with a layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // If user click on the edit icon, take them to the page where they can edit the contact
            R.id.editContact -> {
                val action =
                    ContactDetailsFragmentDirections.actionContactDetailsFragmentToEditContactFragment(
                        contact
                    )
                view?.findNavController()?.navigate(action)
                true
            }

            // If a user click on delete, show a dialog asking them to confirm their choice and
            // perform the necessary action based on their decision.

            R.id.deleteContact -> {
                AlertDialog.Builder(context).also {
                    it.setTitle("Delete this contact?")
                    it.setPositiveButton("DELETE") { _, _ ->
                        viewModel.deleteContact(contact)
                        val action =
                            ContactDetailsFragmentDirections.actionContactDetailsFragmentToContactListFragment()
                        view?.findNavController()?.navigate(action)
                    }
                    it.setNegativeButton("CANCEL") { _, _ -> }

                }.create().show()

                true
            }

            // If a user click on share, share the contact details using implicit intent
            R.id.shareContact -> {
                val shareIntent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.putExtra(Intent.EXTRA_TEXT, "Name: ${name.text}\n" +
                            "Phone Number: ${phoneNumber.text}\nEmail: ${email.text}")

                    this.type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share contact via"))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    // Method to make phone call
    private fun makeCall() {
        val number = Uri.parse("tel: ${phoneNumber.text}")
        val intent = Intent(Intent.ACTION_CALL, number)
        startActivity(intent)
    }

    // Check permission result and proceed accordingly
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             makeCall()
            }
        }
    }
}