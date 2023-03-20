package com.example.roombindingstatexml

sealed interface AddContactDialogEvent {
    data class showToast(val message: String) : AddContactDialogEvent
    object dialogDismiss : AddContactDialogEvent
}