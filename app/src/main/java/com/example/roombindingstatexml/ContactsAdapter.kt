package com.example.roombindingstatexml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roombindingstatexml.databinding.ContactItemBinding

class ContactsAdapter(
    private val onDeleteContact: (Contact) -> Unit
) : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)

        holder.bindData(contact, onDeleteContact)
    }

    class ContactViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(contact: Contact, onDeleteContact: (Contact) -> Unit) {
            binding.apply {
                tvName.text = contact.firstName.plus(contact.lastName)
                tvPhoneNumber.text = contact.phoneNumber

                ivDelete.setOnClickListener {
                    onDeleteContact(contact)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem == newItem
            }
        }
    }
}
