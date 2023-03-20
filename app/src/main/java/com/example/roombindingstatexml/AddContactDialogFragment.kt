package com.example.roombindingstatexml

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.room.Room
import com.example.roombindingstatexml.databinding.FragmentAddContactDialogBinding
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// todo: need to implement dialog view model for screen configuration changes
class AddContactDialogFragment : DialogFragment() {

    private val db by lazy {
        Room.databaseBuilder(
            requireContext(),
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }

    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<ContactDialogViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactDialogViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_contact_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddContactDialogBinding.bind(view)

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        /*binding.etFirstName.doAfterTextChanged {
            //onEvent(ContactUiAction.SetFirstName(it.toString()))
            viewModel.userAction(ContactDialogUiAction.SetFirstName(it.toString()))
        }

        binding.etLastName.doAfterTextChanged {
            //onEvent(ContactUiAction.SetLastName(it.toString()))
            viewModel.userAction(ContactDialogUiAction.SetLastName(it.toString()))
        }

        binding.etPhoneNumber.doAfterTextChanged {
            //onEvent(ContactUiAction.SetPhoneNumber(it.toString()))
            viewModel.userAction(ContactDialogUiAction.SetPhoneNumber(it.toString()))
        }

        binding.btnSave.setOnClickListener {
            //onEvent(ContactUiAction.SaveContactUi)
            viewModel.userAction(ContactDialogUiAction.SaveContact)
            dismiss()
        }*/

        binding.bindState(
            uiState = viewModel.uiState,
            uiAction = viewModel.userAction,
            uiEvent = viewModel.uiEvent
        )
    }

    //bind state
    private fun FragmentAddContactDialogBinding.bindState(
        uiState: StateFlow<SaveContactState>,
        uiAction: (ContactDialogUiAction) -> Unit,
        uiEvent: SharedFlow<AddContactDialogEvent>
    ) {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uiEvent.collectLatest { uiEvent ->

                    when(uiEvent) {
                        AddContactDialogEvent.dialogDismiss -> dismiss()

                        is AddContactDialogEvent.showToast -> {
                            Toast.makeText(requireContext(), uiEvent.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        bindInput(uiAction)

        bindClick(uiAction)
    }

    //bind input
    private fun FragmentAddContactDialogBinding.bindInput(
        uiAction: (ContactDialogUiAction) -> Unit
    ) {
        etFirstName.doAfterTextChanged {
            println("doAfterTextChanged >> first name $it")
            uiAction(ContactDialogUiAction.SetFirstName(it.toString()))
        }

        etLastName.doAfterTextChanged {
            println("doAfterTextChanged >> last name $it")
            uiAction(ContactDialogUiAction.SetLastName(it.toString()))
        }

        etPhoneNumber.doAfterTextChanged {
            println("doAfterTextChanged >> phone number $it")
            uiAction(ContactDialogUiAction.SetPhoneNumber(it.toString()))
        }
    }

    //bind click
    private fun FragmentAddContactDialogBinding.bindClick(
        uiAction: (ContactDialogUiAction) -> Unit
    ) {
        btnSave.setOnClickListener {
            uiAction(ContactDialogUiAction.SaveContact)
        }
    }
}