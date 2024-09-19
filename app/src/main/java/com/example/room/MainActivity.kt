package com.example.room

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.room.data.database.MyDatabase
import com.example.room.data.database.Person
import com.example.room.data.database.PersonDao
import com.example.room.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), EditPersonDialog.UpdateDialogListener{
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var binding : ActivityMainBinding
    private lateinit var persons:MutableList<Person>
    private lateinit var adapter:PersonRecyclerViewAdapter

    private lateinit var db: MyDatabase
    private lateinit var personDao: PersonDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return  false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deletePerson(position)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }


        }

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.personRecycler)

        persons = mutableListOf()
        initializeRecycler()

        db = Room.databaseBuilder(applicationContext, MyDatabase::class.java, "database").build()

        personDao = db.personDao()
        getPersons()

        setCreateButtonListener()
        setSearchButtonListener()
    }


    private fun getPersons() {
        clearPersons()
        lifecycleScope.launch {
            val result = personDao.getAllPersons()
            result.forEach {
                persons.add(it)
                adapter.notifyItemInserted(persons.size - 1)
            }
        }
    }

    private fun setCreateButtonListener(){
        binding.addButton.setOnClickListener {
            if (!checkInputs()){
                return@setOnClickListener
            }

            val name = binding.nameEdit.text.toString()
            val age = binding.ageEdit.text.toString().toInt()

            lifecycleScope.launch {
                val id = personDao.createPerson(Person(name = name, age = age))
                persons.add(Person(id, name, age))
                adapter.notifyItemInserted(persons.size - 1)
            }
            clearInputs()
        }
    }

    private fun setSearchButtonListener(){
        binding.searchButton.setOnClickListener {
            if (binding.searchName.text.isBlank()){
                getPersons()
            }else{
                getPersonByName(binding.searchName.text.toString())
                clearInputs()
            }
        }
    }

    private fun deletePerson(position:Int){
        val person = persons[position]
        lifecycleScope.launch {
            personDao.deletePerson(person)
            persons.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

    override fun onPersonUpdated(person: Person) {
        lifecycleScope.launch {
            personDao.updatePerson(person)
            getPersons()
        }
    }

    private fun getPersonByName(name:String){
        lifecycleScope.launch {
            val resultPersons = personDao.getPersonByName(name)
            clearPersons()
            if (resultPersons.isNotEmpty()){
                resultPersons.forEach {
                    persons.add(it)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun clearInputs(){
        binding.nameEdit.text.clear()
        binding.ageEdit.text.clear()
        binding.searchName.text.clear()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.nameEdit.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.ageEdit.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.searchName.windowToken, 0)
    }

    private fun checkInputs(): Boolean{
        if (binding.nameEdit.text.isBlank() || binding.ageEdit.text.isBlank()){
            return false
        }
        return true
    }

    private fun initializeRecycler(){
        adapter = PersonRecyclerViewAdapter(persons){ position ->
            val updateDialog = EditPersonDialog(this, persons[position])
            updateDialog.show(supportFragmentManager, updateDialog.tag)
        }
        binding.personRecycler.layoutManager = LinearLayoutManager(this)
        binding.personRecycler.adapter = adapter
    }

    private fun clearPersons(){
        persons.clear()
        adapter.notifyDataSetChanged()
    }
}
