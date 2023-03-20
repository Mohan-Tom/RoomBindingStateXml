package com.example.roombindingstatexml

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactDialogViewModel(
    private val dao: ContactDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveContactState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SaveContactState()
    )

    private val _uiEvent = MutableSharedFlow<AddContactDialogEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val userAction: (ContactDialogUiAction) -> Unit

    init {
        userAction = { userAction ->
            onUIAction(userAction)
        }
    }

    private fun onUIAction(action: ContactDialogUiAction) {

        when(action) {
            is ContactDialogUiAction.SetFirstName -> {
                println("SaveContact >> action first name ${action.firstName}")

                _uiState.update {
                    it.copy(
                        firstName = action.firstName
                    )
                }
            }
            is ContactDialogUiAction.SetLastName -> {
                println("SaveContact >> action last name ${action.lastName}")

                _uiState.update {
                    it.copy(
                        lastName = action.lastName
                    )
                }
            }
            is ContactDialogUiAction.SetPhoneNumber -> {
                println("SaveContact >> action phone number ${action.phoneNumber}")

                _uiState.update {
                    it.copy(
                        phoneNumber = action.phoneNumber
                    )
                }
            }

            ContactDialogUiAction.SaveContact -> {
                val firstName = uiState.value.firstName
                val lastName = uiState.value.lastName
                val phoneNumber = uiState.value.phoneNumber

                println("SaveContact >> $firstName, $lastName, $phoneNumber")

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    sendEvent(AddContactDialogEvent.showToast("Fill the fields"))
                    return
                }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                println("SaveContact >> $contact")

                viewModelScope.launch {
                    dao.upsertContact(contact)
                }

                _uiState.update {
                    it.copy(
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }

                sendEvent(AddContactDialogEvent.showToast("Success"))
                sendEvent(AddContactDialogEvent.dialogDismiss)
            }
        }
    }

    private fun sendEvent(event: AddContactDialogEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}