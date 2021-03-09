package dev.decagon.godday.contacts.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.decagon.godday.contacts.R
import dev.decagon.godday.contacts.adapter.ContactAdapter
import dev.decagon.godday.contacts.databinding.FragmentContactListBinding
import dev.decagon.godday.contacts.viewmodel.ContactsViewModel


/**
 * This is the fragment that displays the list of contacts retrieved from the database
 * it serves as the major navigation gateway to all other pages of the app.
 * It captures the data from view model and passes it to the ContactAdapter.
 */

class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContactsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private val adapter = ContactAdapter()


    // Setting up the menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    // Creating the fragment view and hooking up the view model
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ContactsViewModel::class.java)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setting up the recycler view
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Feeding the recycler view with the data from the view model
        viewModel.getContact()
        viewModel.contacts.observe(viewLifecycleOwner, {
            adapter.setContact(it)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Navigate to CreateContactFragment when the floating action button is clicked
        addButton = binding.fab
        addButton.setOnClickListener {
            val action = ContactListFragmentDirections.actionContactListFragmentToCreateContactFragment()
            it.findNavController().navigate(action)
        }
    }

    // Setting the _binding to null since it's no longer useful at this point
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Inflating the menu with layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contact_list_menu, menu)
    }

    // Setting up the menu item to navigate to the PhoneContactFragment when it is clicked
    // Or exit the app when exit is clicked.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.openPhoneContact -> {
                val action =
                    ContactListFragmentDirections.actionContactListFragmentToPhoneContactFragment()
                view?.findNavController()?.navigate(action)
            }

            R.id.exitApp -> activity?.finish()
        }
        return true
    }

}