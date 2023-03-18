package com.example.roombindingstatexml

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.room.Room
import com.example.roombindingstatexml.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    private val contactAdapter by lazy {
        ContactsAdapter(::deleteContact)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUI()
        observers()
    }

    private fun setupUI() {
        binding.apply {
            rvContacts.adapter = contactAdapter
        }

        binding.scrollRadioGroup.isHorizontalScrollBarEnabled = false

        binding.fabNew.setOnClickListener {
            newContactDialog()
        }

        binding.rgSort.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.rbFirstName -> {
                    sortContacts(SortType.FIRST_NAME)
                }
                R.id.rbLastName -> {
                    sortContacts(SortType.LAST_NAME)
                }
                R.id.rbPhoneNumber -> {
                    sortContacts(SortType.PHONE_NUMBER)
                }
            }
        }
    }

    private fun newContactDialog() {
        viewModel.onEvent(
            event = ContactEvent.ShowDialog
        )
    }

    private fun deleteContact(contact: Contact) {
        viewModel.onEvent(
            event = ContactEvent.DeleteContact(contact)
        )
    }

    private fun sortContacts(sortType: SortType) {
        viewModel.onEvent(
            event = ContactEvent.SortContacts(sortType)
        )
    }

    private fun observers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collectLatest { state ->

                    println("State >> $state")

                    if(state.isAddingContact) {
                        AddContactDialogFragment(
                            onEvent = viewModel::onEvent
                        ).also {
                            it.show(supportFragmentManager, it.tag)
                        }
                    }

                    contactAdapter.contactList = state.contacts
                }
            }
        }
    }
}