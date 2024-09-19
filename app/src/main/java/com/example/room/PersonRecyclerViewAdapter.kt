package com.example.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.room.data.database.Person
import com.example.room.databinding.PersonCardBinding

class PersonRecyclerViewAdapter(private val persons:MutableList<Person>, val onClick: (Int) -> Unit) : RecyclerView.Adapter<PersonRecyclerViewAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonRecyclerViewAdapter.MyViewHolder {
        val binding = PersonCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonRecyclerViewAdapter.MyViewHolder, position: Int) {
        val person = persons[position]
        holder.bind(person, onClick, position)
    }

    override fun getItemCount(): Int {
        return persons.size
    }

    class MyViewHolder (private val binding:PersonCardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(person: Person, onClick: (Int) -> Unit, position: Int){
            binding.nameText.text = person.name
            binding.ageText.text = person.age.toString()
            binding.personCard.setOnClickListener{
                onClick(position)
            }
        }
    }
}