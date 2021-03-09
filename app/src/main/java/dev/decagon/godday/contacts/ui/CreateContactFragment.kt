package dev.decagon.godday.contacts.ui

import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.util.PatternsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import dev.decagon.godday.contacts.R
import dev.decagon.godday.contacts.databinding.FragmentCreateContactBinding
import dev.decagon.godday.contacts.model.Contact
import dev.decagon.godday.contacts.viewmodel.ContactsViewModel
import java.util.regex.Pattern


/**
 * This is the page where the user can add/create new contacts. Contacts that are saved here are
 * saved to the database. This page validate for email address to guide against illegal inputs.
 */

class CreateContactFragment : Fragment() {

    private var _binding: FragmentCreateContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContactsViewModel
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phoneNumber: EditText

    // Notify the fragment that it has a menu option
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Create the view and initialise the view model
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateContactBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        return binding.root
    }

    // Binding view components
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        name = binding.name
        email = binding.email
        phoneNumber = binding.phoneNumber
    }

    // Setting the _binding to null since it's no longer useful at this point
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inflate the menu with layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_contact_menu, menu)
    }

    // Setup up the menu item to validate, save and navigate to the next fragment based on the user's
    // actions

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saveContact -> {

                // Extract the contacts details
                val contactName = name.text.toString().trim()
                val contactPhone = phoneNumber.text.toString().trim()
                val contactEmail = email.text.toString().trim()

                // If all input fields are empty, display a message using Toast, and navigate back
                // to the contact list page without sending any data to the database.

                if (contactName.isEmpty() && contactPhone.isEmpty() && contactEmail.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Contact information is null and cannot be saved",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    val action =
                        CreateContactFragmentDirections.actionCreateContactFragmentToContactListFragment()
                    view?.findNavController()?.navigate(action)
                    return false
                }

                /**
                 * If the email field is empty or a valid email address is entered, proceed and save the
                 * contact to database using the helper method provided by the view model. Then navigate to the
                 * details page where user can view the details they just save and edit if necessary.
                 */

                if (contactEmail.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
                    val contact = Contact().also {
                        it.name = contactName
                        it.phoneNumber = contactPhone
                        it.email = contactEmail
                    }
                    viewModel.addContact(contact)

                    val action =
                        CreateContactFragmentDirections.actionCreateContactFragmentToContactDetailsFragment(contact)
                    Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(action)
                    name.text.clear()
                    phoneNumber.text.clear()
                    email.text.clear()

                } else {
                    Toast.makeText(context, "Cannot save invalid email", Toast.LENGTH_SHORT).show()
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}