package dev.decagon.godday.contacts.ui

import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dev.decagon.godday.contacts.R
import dev.decagon.godday.contacts.databinding.FragmentEditContactBinding
import dev.decagon.godday.contacts.model.Contact
import dev.decagon.godday.contacts.viewmodel.ContactsViewModel


/**
 * This is the page where the user can edit/update already existing contacts. Contacts that are edited here are
 * updated in the database. This page validate for email address to guide against illegal inputs.
 */

class EditContactFragment : Fragment() {

    private var _binding: FragmentEditContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContactsViewModel
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var contact: Contact
    private val args by navArgs<EditContactFragmentArgs>()

    // Indicate that the fragment has a menu
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
        _binding = FragmentEditContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Display the details to be edited
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        name = binding.name
        name.setText(contact.name)

        email = binding.email
        email.setText(contact.email)

        phoneNumber = binding.phoneNumber
        phoneNumber.setText(contact.phoneNumber)
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
                val contactName = name.text.toString().trim()
                val contactPhone = phoneNumber.text.toString().trim()
                val contactEmail = email.text.toString().trim()

                if (contactName.isEmpty() && contactPhone.isEmpty() && contactEmail.isEmpty()) {
                    Toast.makeText(context, "Couldn't save contact changes", Toast.LENGTH_SHORT)
                        .show()
                    return false
                }

                /**
                 * If the email field is empty or a valid email address is entered, proceed and save the
                 * changes to database using the helper method provided by the view model. Then navigate to the
                 * details page where user can view the details they just save and edit if necessary.
                 */

                 if (contactEmail.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
                    contact.name = contactName
                    contact.phoneNumber = contactPhone
                    contact.email = contactEmail
                    viewModel.editContact(contact)

                    val action = EditContactFragmentDirections.actionEditContactFragmentToContactDetailsFragment(contact)
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