package com.example.roombindingstatexml.contacts_screen

import com.example.roombindingstatexml.Contact

sealed interface ContactUiAction {
    object ShowDialog: ContactUiAction
    object HideDialog: ContactUiAction
    data class SortContacts(val sortType: SortType): ContactUiAction
    data class DeleteContactUi(val contact: Contact): ContactUiAction
}