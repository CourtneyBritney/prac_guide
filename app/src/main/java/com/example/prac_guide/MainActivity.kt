package com.example.prac_guide





import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val itemNames = arrayListOf("Passport", "Shoes", "Toothbrush")
    private val itemCategories = arrayListOf("Documents", "Clothing", "Toiletries")
    private val itemQuantities = arrayListOf(1, 2, 1)
    private val itemComments = arrayListOf("Don't forget this!", "Walking shoes", "Travel size")

    private val MAX_NAME_LEN = 40
    private val MAX_CAT_LEN = 30
    private val MAX_QTY = 999
    private val MAX_COMMENT_LEN = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEt = findViewById<EditText>(R.id.etName)
        val catEt = findViewById<EditText>(R.id.etCategory)
        val qtyEt = findViewById<EditText>(R.id.etQuantity)
        val commentEt = findViewById<EditText>(R.id.etComment)
        val addBtn = findViewById<Button>(R.id.btnAdd)
        val viewListBtn = findViewById<Button>(R.id.btnViewList)
        val listView = findViewById<ListView>(R.id.listView)
        val backBtn = findViewById<Button>(R.id.btnBack)
        val clearAllBtn = findViewById<Button>(R.id.btnClearAll)
        val screenAdd = findViewById<LinearLayout>(R.id.screenAdd)
        val screenList = findViewById<LinearLayout>(R.id.screenList)

        nameEt.filters = arrayOf(InputFilter.LengthFilter(MAX_NAME_LEN))
        catEt.filters = arrayOf(InputFilter.LengthFilter(MAX_CAT_LEN))
        qtyEt.filters = arrayOf(InputFilter.LengthFilter(3))
        commentEt.filters = arrayOf(InputFilter.LengthFilter(MAX_COMMENT_LEN))

        viewListBtn.isEnabled = itemNames.isNotEmpty()

        fun watcherFor(et: EditText): TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!et.text.isNullOrBlank()) et.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        listOf(nameEt, catEt, qtyEt, commentEt).forEach { it.addTextChangedListener(watcherFor(it)) }

        fun showFieldError(field: EditText, message: String) {
            field.error = message
            field.requestFocus()
            field.setSelection(field.text?.length ?: 0)
        }

        fun clearInputs() {
            nameEt.text.clear()
            catEt.text.clear()
            qtyEt.text.clear()
            commentEt.text.clear()
            nameEt.requestFocus()
        }

        fun validateInputs(): Boolean {
            val name = nameEt.text.toString().trim()
            val category = catEt.text.toString().trim()
            val qtyText = qtyEt.text.toString().trim()
            val comment = commentEt.text.toString().trim()

            fun isLettersSpaces(s: String) = s.matches(Regex("^[\\p{L}][\\p{L} ]*[\\p{L}]$"))
            fun hasVowel(s: String) = s.contains(Regex("[AEIOUaeiou]"))

            if (name.isEmpty()) {
                showFieldError(nameEt, "Enter an item name (e.g., Passport).")
                return false
            }
            if (name.length !in 2..MAX_NAME_LEN || !isLettersSpaces(name) || !hasVowel(name)) {
                showFieldError(nameEt, "Enter a real word with letters (e.g., Passport).")
                return false
            }

            if (category.isEmpty()) {
                showFieldError(catEt, "Enter a category (e.g., Clothing).")
                return false
            }
            if (category.length !in 3..MAX_CAT_LEN || !isLettersSpaces(category) || !hasVowel(category)) {
                showFieldError(catEt, "Enter a valid category word (e.g., Toiletries).")
                return false
            }

            if (qtyText.isEmpty()) {
                showFieldError(qtyEt, "Enter a quantity (whole number).")
                Toast.makeText(this, "Tip: Use numbers like 1, 2, 3…", Toast.LENGTH_SHORT).show()
                return false
            }
            val qty = qtyText.toIntOrNull()
            if (qty == null || qty < 1 || qty > MAX_QTY) {
                showFieldError(qtyEt, "Quantity must be between 1 and $MAX_QTY.")
                return false
            }

            if (comment.length > MAX_COMMENT_LEN) {
                showFieldError(commentEt, "Comment too long (max $MAX_COMMENT_LEN).")
                return false
            }

            val duplicate = itemNames.indices.any { i ->
                itemNames[i].equals(name, ignoreCase = true) &&
                        itemCategories[i].equals(category, ignoreCase = true)
            }
            if (duplicate) {
                Toast.makeText(
                    this,
                    "Item already exists in this category. Try increasing quantity instead.",
                    Toast.LENGTH_LONG
                ).show()
                showFieldError(nameEt, "Duplicate item. Change name/category or adjust quantity.")
                return false
            }
            return true
        }

        addBtn.setOnClickListener {
            if (!validateInputs()) return@setOnClickListener

            val name = nameEt.text.toString().trim()
            val category = catEt.text.toString().trim()
            val qty = qtyEt.text.toString().trim().toInt()
            val comment = commentEt.text.toString().trim().ifEmpty { "—" }

            itemNames.add(name)
            itemCategories.add(category)
            itemQuantities.add(qty)
            itemComments.add(comment)

            Toast.makeText(
                this,
                "Added: $name ($category) × $qty\nNext: Tap “View Packing List” to see all items.",
                Toast.LENGTH_LONG
            ).show()

            clearInputs()
            viewListBtn.isEnabled = true
        }

        viewListBtn.setOnClickListener {
            if (itemNames.isEmpty()) {
                Toast.makeText(
                    this,
                    "Your list is empty. Add your first item on the previous screen.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val display = ArrayList<String>()
            for (i in itemNames.indices) {
                display.add("${itemNames[i]} (${itemCategories[i]}) • Qty: ${itemQuantities[i]} • ${itemComments[i]}")
            }

            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, display)
            screenAdd.visibility = View.GONE
            screenList.visibility = View.VISIBLE
        }

        // 🟩 NEW: Clear All button functionality
        clearAllBtn.setOnClickListener {
            if (itemNames.isEmpty()) {
                Toast.makeText(this, "There’s nothing to clear yet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Clear All Items?")
                .setMessage("This will remove every item from your packing list. Are you sure?")
                .setPositiveButton("Yes, Clear All") { _, _ ->
                    itemNames.clear()
                    itemCategories.clear()
                    itemQuantities.clear()
                    itemComments.clear()
                    listView.adapter = null
                    viewListBtn.isEnabled = false
                    Toast.makeText(this, "All items cleared.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        backBtn.setOnClickListener {
            screenList.visibility = View.GONE
            screenAdd.visibility = View.VISIBLE
        }
    }
}
