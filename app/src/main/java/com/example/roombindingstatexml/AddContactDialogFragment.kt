package com.example.roombindingstatexml

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.roombindingstatexml.databinding.FragmentAddContactDialogBinding

// todo: need to implement dialog view model for screen configuration changes
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

        binding.etFirstName.doAfterTextChanged {
            onEvent(ContactUiAction.SetFirstName(it.toString()))
        }

        binding.etLastName.doAfterTextChanged {
            onEvent(ContactUiAction.SetLastName(it.toString()))
        }

        binding.etPhoneNumber.doAfterTextChanged {
            onEvent(ContactUiAction.SetPhoneNumber(it.toString()))
        }

        binding.btnSave.setOnClickListener {
            onEvent(ContactUiAction.SaveContactUi)
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onEvent(ContactUiAction.HideDialog)
    }
}