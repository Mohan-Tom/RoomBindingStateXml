package com.example.roombindingstatexml

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.roombindingstatexml.databinding.FragmentAddContactDialogBinding

class AddContactDialogFragment(
    private val onEvent: (ContactUiAction) -> Unit = {}
) : DialogFragment() {

    private val binding by lazy { FragmentAddContactDialogBinding.inflate(LayoutInflater.from(context)) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEvent(
                    ContactUiAction.SetFirstName(p0.toString())
                )
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })

        binding.etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEvent(
                    ContactUiAction.SetLastName(p0.toString())
                )
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })

        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                onEvent(
                    ContactUiAction.SetPhoneNumber(p0.toString())
                )
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })

        binding.btnSave.setOnClickListener {
            onEvent(
                ContactUiAction.SaveContactUi
            )
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onEvent(ContactUiAction.HideDialog)
    }
}