package com.example.sg.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.sg.R
import com.example.sg.data.models.User
import com.example.sg.databinding.DialogCreateUserBinding

class CreateUserDialog(private val onButtonClick: (User) -> Unit) : DialogFragment() {

    private lateinit var binding: DialogCreateUserBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCreateUserBinding.inflate(LayoutInflater.from(requireContext()))

        val createUserButton = binding.btnCreateUser
        createUserButton.setOnClickListener {
            if (validateInput()) {
                val user = User(
                    name = binding.editTextName.text.toString(),
                    email = binding.editTextEmail.text.toString(),
                    gender = binding.spinnerGender.selectedItem.toString(),
                    status = binding.spinnerStatus.selectedItem.toString()
                )
                onButtonClick(user)
                dismiss()
            }
        }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setTitle("Create User")
            .setPositiveButton("Close") { _, _ -> dismiss() }
            .create()
    }

    private fun validateInput(): Boolean {
        return binding.editTextName.text.isNotBlank() && binding.editTextEmail.text.isNotBlank()
    }
}