package com.example.mad_project

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import com.example.mad_project.R
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import android.app.DatePickerDialog
import android.widget.DatePicker

class CreateGoalActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_goal)

        val nameEditText = findViewById<EditText>(R.id.name_edit_text)
        val amountEditText = findViewById<EditText>(R.id.amount_edit_text)
        val descriptionEditText = findViewById<EditText>(R.id.description_edit_text)
        val categoryEditText = findViewById<EditText>(R.id.category_edit_text)
        val completionDateEditText = findViewById<EditText>(R.id.completion_date_edit_text)

        val createButton = findViewById<Button>(R.id.create_button)
        createButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val description = descriptionEditText.text.toString()
            val category = categoryEditText.text.toString()
            val completionDate = completionDateEditText.text.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (name.isEmpty() || amount == null || description.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new goal with the given data
            val goal = hashMapOf(
                "userId" to userId,
                "name" to name,
                "amount" to amount,
                "description" to description,
                "category" to category,
                "completionDate" to completionDate,
                "created_at" to Calendar.getInstance().timeInMillis
            )

            // Add the goal to the "goals" collection in Firestore
            db.collection("goals")
                .add(goal)
                .addOnSuccessListener {
                    // Display a success message or go back to the previous screen
                    Toast.makeText(this, "Goal added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    // Display an error message
                    TODO()
                }
        }

        completionDateEditText.setOnClickListener {
            showDatePicker()
        }

        val nav: NavigationBarView = findViewById(R.id.navbar)
        nav.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.home -> {
                        val intent = Intent(this@CreateGoalActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.goals -> {
                        val intent = Intent(this@CreateGoalActivity, HomePageActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.stats -> {
                        val intent = Intent(this@CreateGoalActivity, ViewPage::class.java)
                        startActivity(intent)
                    }
                    R.id.settings -> {
                        val intent = Intent(this@CreateGoalActivity, profile::class.java)
                        startActivity(intent)
                    }
                }
                return true
            }
        })
    }

    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)
        val completionDateEditText = findViewById<EditText>(R.id.completion_date_edit_text)
        completionDateEditText.setText(formattedDate)
    }
}