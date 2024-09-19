package com.example.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.room.data.database.Person
import com.example.room.databinding.EditPersonDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditPersonDialog(private val listener:UpdateDialogListener, val person: Person) : BottomSheetDialogFragment(){
    private lateinit var binding:EditPersonDialogBinding

    interface UpdateDialogListener {
        fun onPersonUpdated(person: Person)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditPersonDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateName.setText(person.name)
        binding.updateAge.setText(person.age.toString())

        binding.updatePersonButton.setOnClickListener{
            if (binding.updateName.text.isBlank() || binding.updateAge.text.isBlank()){
                Toast.makeText(this.context, "Fields can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            listener.onPersonUpdated((Person(person.id, binding.updateName.text.toString(), binding.updateAge.text.toString().toInt())))
            dismiss()
        }
    }
}