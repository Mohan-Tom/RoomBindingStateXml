package com.example.roombindingstatexml.new_contact_dialog

data class SaveContactState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dialogDismiss: Boolean = false
)
