package com.example.prac_guide

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//class MainActivity : AppCompatActivity() {
  //  override fun onCreate(savedInstanceState: Bundle?) {
    //    super.onCreate(savedInstanceState)
      //  enableEdgeToEdge()
        //setContentView(R.layout.activity_main)





        class MainActivity : AppCompatActivity() {

            // Four parallel arrays
            private val itemNames = arrayListOf("Passport", "Shoes", "Toothbrush")
            private val itemCategories = arrayListOf("Documents", "Clothing", "Toiletries")
            private val itemQuantities = arrayListOf(1, 2, 1)
            private val itemComments = arrayListOf("Don't forget this!", "For walking", "Travel size")

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)

                // Screen 1 (add items)
                val nameEt = findViewById<EditText>(R.id.etName)
                val catEt = findViewById<EditText>(R.id.etCategory)
                val qtyEt = findViewById<EditText>(R.id.etQuantity)
                val commentEt = findViewById<EditText>(R.id.etComment)
                val addBtn = findViewById<Button>(R.id.btnAdd)
                val viewListBtn = findViewById<Button>(R.id.btnViewList)

                // Screen 2 (list view)
                val listView = findViewById<ListView>(R.id.listView)
                val backBtn = findViewById<Button>(R.id.btnBack)

                val screenAdd = findViewById<LinearLayout>(R.id.screenAdd)
                val screenList = findViewById<LinearLayout>(R.id.screenList)

                // Add to list
                addBtn.setOnClickListener {
                    val name = nameEt.text.toString().trim()
                    val category = catEt.text.toString().trim()
                    val qtyText = qtyEt.text.toString().trim()
                    val comment = commentEt.text.toString().trim()

                    if (name.isEmpty() || category.isEmpty() || qtyText.isEmpty()) {
                        Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val qty = try {
                        qtyText.toInt()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this, "Quantity must be a number.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    if (qty < 1) {
                        Toast.makeText(this, "Quantity must be at least 1.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Add values to arrays
                    itemNames.add(name)
                    itemCategories.add(category)
                    itemQuantities.add(qty)
                    itemComments.add(if (comment.isEmpty()) "—" else comment)

                    // Reset fields
                    nameEt.text.clear()
                    catEt.text.clear()
                    qtyEt.text.clear()
                    commentEt.text.clear()
                    nameEt.requestFocus()

                    Toast.makeText(this, "Added: $name ($category) x$qty", Toast.LENGTH_SHORT).show()
                }

                // Navigate to screen 2 (show list)
                viewListBtn.setOnClickListener {
                    val displayList = ArrayList<String>()
                    for (i in itemNames.indices) {
                        displayList.add("${itemNames[i]} (${itemCategories[i]}) • Qty: ${itemQuantities[i]} • ${itemComments[i]}")
                    }
                    listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
                    screenAdd.visibility = View.GONE
                    screenList.visibility = View.VISIBLE
                }

                // Back to add screen
                backBtn.setOnClickListener {
                    screenList.visibility = View.GONE
                    screenAdd.visibility = View.VISIBLE
                }
            }
        }

