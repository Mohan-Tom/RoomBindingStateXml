package com.example.roombindingstatexml.new_contact_dialog

sealed interface ContactDialogUiAction {
    class SetFirstName(val firstName: String) : ContactDialogUiAction
    class SetLastName(val lastName: String) : ContactDialogUiAction
    class SetPhoneNumber(val phoneNumber: String) : ContactDialogUiAction
    object SaveContact : ContactDialogUiAction
}