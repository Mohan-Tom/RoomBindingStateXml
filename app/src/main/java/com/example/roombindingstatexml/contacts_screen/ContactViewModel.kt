package com.example.roombindingstatexml.contacts_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roombindingstatexml.ContactDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDao
): ViewModel() {

    private val _uiState = MutableStateFlow(ContactState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContactState()
    )

    //UI action from UI layer
    val accept: (ContactUiAction) -> Unit

    init {
        //produce sort type
        val sortTypeFlow = uiState.map { it.sortType }
            .distinctUntilChanged()

        //collect sort type and produce contacts based on sort type
        val contactsFlow = sortTypeFlow
            .flatMapLatest { sortType ->
                when(sortType) {
                    SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
                    SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
                    SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
                }
            }
            .distinctUntilChanged()

        //observe contacts and produce to UI state
        contactsFlow.onEach { contacts ->
            println("contactsFlow >> $contacts")
                _uiState.update { state ->
                    state.copy(
                        contacts = contacts
                    )
                }
            }.launchIn(viewModelScope)

        //user actions
        accept = { uiAction -> onUiAction(uiAction) }
    }

    //UI actions
    private fun onUiAction(event: ContactUiAction) {
        when(event) {
            is ContactUiAction.DeleteContactUi -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactUiAction.HideDialog -> {
                _uiState.update {
                    it.copy(
                        isAddingContact = false
                    )
                }
            }
            is ContactUiAction.ShowDialog -> {
                _uiState.update { it.copy(
                    isAddingContact = true
                ) }
            }
            is ContactUiAction.SortContacts -> {
                _uiState.update { state ->
                    state.copy(
                        sortType = event.sortType
                    )
                }
            }
        }
    }
}