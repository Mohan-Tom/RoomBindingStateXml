package com.example.roombindingstatexml.new_contact_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.roombindingstatexml.ContactDatabase
import com.example.roombindingstatexml.R
import com.example.roombindingstatexml.databinding.FragmentAddContactDialogBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddContactDialogFragment : DialogFragment() {

    private var onDismissListener: ((Boolean) -> Unit)? = null

    private val db: ContactDatabase by lazy {
       /* Room.databaseBuilder(
            requireContext(),
            ContactDatabase::class.java,
            "contacts.db"
        ).build()*/
        ContactDatabase.getInstance(requireContext())
    }

    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<ContactDialogViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactDialogViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_contact_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddContactDialogBinding.bind(view)

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.bindState(
            uiState = viewModel.uiState,
            uiAction = viewModel.userAction,
            uiEvent = viewModel.uiEvent
        )
    }

    //bind state
    private fun FragmentAddContactDialogBinding.bindState(
        uiState: StateFlow<SaveContactState>,
        uiAction: (ContactDialogUiAction) -> Unit,
        uiEvent: SharedFlow<AddContactDialogEvent>
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                uiEvent.collectLatest { uiEvent ->

                    when(uiEvent) {

                        /*AddContactDialogEvent.DialogDismiss -> {
                            dismissPopup(true)
                        }*/

                        is AddContactDialogEvent.ShowToast -> {
                            Toast.makeText(requireContext(), uiEvent.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        val dialogDismissFLow = uiState.map {
            it.dialogDismiss
        }.distinctUntilChanged()

        //dialog dismiss
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                dialogDismissFLow.collectLatest { isDismiss ->
                    println("dialogDismissFLow >> $isDismiss")

                    if(isDismiss) dismiss()
                }
            }
        }

        bindInput(uiAction)

        bindClick(uiAction)
    }

    //bind input
    private fun FragmentAddContactDialogBinding.bindInput(
        uiAction: (ContactDialogUiAction) -> Unit
    ) {
        etFirstName.doAfterTextChanged {
            uiAction(ContactDialogUiAction.SetFirstName(it.toString()))
        }

        etLastName.doAfterTextChanged {
            uiAction(ContactDialogUiAction.SetLastName(it.toString()))
        }

        etPhoneNumber.doAfterTextChanged {
            uiAction(ContactDialogUiAction.SetPhoneNumber(it.toString()))
        }
    }

    //bind click
    private fun FragmentAddContactDialogBinding.bindClick(
        uiAction: (ContactDialogUiAction) -> Unit
    ) {
        btnSave.setOnClickListener {
            uiAction(ContactDialogUiAction.SaveContact)
        }
    }

    /*private fun dismissPopup(succeeded: Boolean = false) {
        dismiss()
        onDismissListener?.invoke(succeeded)
    }*/

    class Builder {
        private val d = AddContactDialogFragment()

        fun setOnDismissListener(onDismissListener: (Boolean) -> Unit): Builder {
            d.onDismissListener = onDismissListener
            return this
        }

        fun build(): AddContactDialogFragment {
            return d
        }
    }
}