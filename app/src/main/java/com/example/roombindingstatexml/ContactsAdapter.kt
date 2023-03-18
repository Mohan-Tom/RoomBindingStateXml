package com.example.roombindingstatexml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roombindingstatexml.databinding.ContactItemBinding

class ContactsAdapter(
    private val deleteContact: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[holder.bindingAdapterPosition]

        holder.bindData(contact)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ContactViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(contact: Contact) {
            binding.apply {
                tvName.text = contact.firstName.plus(contact.lastName)
                tvPhoneNumber.text = contact.phoneNumber

                ivDelete.setOnClickListener {
                    deleteContact(contact)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    var contactList: List<Contact>
        get() = differ.currentList
        set(value) = differ.submitList(value)
}