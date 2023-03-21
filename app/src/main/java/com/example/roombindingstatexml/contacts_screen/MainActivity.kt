package com.example.roombindingstatexml.contacts_screen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.roombindingstatexml.ContactDatabase
import com.example.roombindingstatexml.R
import com.example.roombindingstatexml.databinding.ActivityMainBinding
import com.example.roombindingstatexml.new_contact_dialog.AddContactDialogFragment
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val db: ContactDatabase by lazy {

        /*Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()*/

        ContactDatabase.getInstance(applicationContext)
    }

    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    private var onAddContactClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bindState(
            uiState = viewModel.uiState,
            uiAction = viewModel.accept
        )
    }

    private fun ActivityMainBinding.bindState(
        uiState: StateFlow<ContactState>,
        uiAction: (ContactUiAction) -> Unit
    ) {
        val contactsAdapter = ContactsAdapter { deletedContact ->
            uiAction(ContactUiAction.DeleteContactUi(deletedContact))
        }

        //scrollRadioGroup.isHorizontalScrollBarEnabled = false

        rgSort.setOnCheckedChangeListener { _, id ->
            uiAction(
                ContactUiAction.SortContacts(
                    when (id) {
                        R.id.rbFirstName -> {
                            SortType.FIRST_NAME
                        }
                        R.id.rbLastName -> {
                            SortType.LAST_NAME
                        }
                        R.id.rbPhoneNumber -> {
                            SortType.PHONE_NUMBER
                        }
                        else -> SortType.FIRST_NAME
                    }
                )
            )
        }

        //is show dialog flow
        val isAddingContactFlow = uiState.map { it.isAddingContact }
            .distinctUntilChanged()

        lifecycleScope.launch {
            isAddingContactFlow.collectLatest { isAddingContact ->

                //println("isAddingContact >> $isAddingContact")

                if (isAddingContact && onAddContactClicked) {
                    AddContactDialogFragment.Builder()
                        .setOnDismissListener { isSuccess ->
                            if(isSuccess) {
                                Toast.makeText(this@MainActivity, "Succeeded", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .build()
                        .also {
                            it.show(supportFragmentManager, it.tag)
                            onAddContactClicked = false
                        }
                    uiAction(ContactUiAction.HideDialog)
                }
            }
        }

        bindList(
            adapter = contactsAdapter,
            uiState = uiState,
        )

        bindClick(
            uiAction = uiAction
        )
    }

    private fun ActivityMainBinding.bindList(
        adapter: ContactsAdapter,
        uiState: StateFlow<ContactState>
    ) {
        rvContacts.adapter = adapter

        //list flow
        val contactsFlow = uiState.map { it.contacts }
            .distinctUntilChanged()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                contactsFlow.collectLatest(adapter::submitList)
            }
        }
    }

    private fun ActivityMainBinding.bindClick(uiAction: (ContactUiAction) -> Unit) {
        fabNew.setOnClickListener {
            onAddContactClicked = true
            uiAction(ContactUiAction.ShowDialog)
        }
    }
}