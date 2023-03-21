package com.example.roombindingstatexml.new_contact_dialog

sealed interface AddContactDialogEvent {
    data class ShowToast(val message: String) : AddContactDialogEvent
}