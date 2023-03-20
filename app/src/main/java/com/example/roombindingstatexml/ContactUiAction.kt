package com.example.roombindingstatexml

sealed interface ContactUiAction {
    object SaveContactUi: ContactUiAction
    data class SetFirstName(val firstName: String): ContactUiAction
    data class SetLastName(val lastName: String): ContactUiAction
    data class SetPhoneNumber(val phoneNumber: String): ContactUiAction
    object ShowDialog: ContactUiAction
    object HideDialog: ContactUiAction
    data class SortContacts(val sortType: SortType): ContactUiAction
    data class DeleteContactUi(val contact: Contact): ContactUiAction
}