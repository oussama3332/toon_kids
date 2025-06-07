package com.example.kidsapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BlockedLinksActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var list: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_links)

        val input = findViewById<EditText>(R.id.blockLinkEditText)
        val addBtn = findViewById<Button>(R.id.addBlockButton)
        val listView = findViewById<ListView>(R.id.blockedLinksList)
        val clearBtn = findViewById<Button>(R.id.clearBlockedButton)

        val prefs = getSharedPreferences("BlockedPrefs", MODE_PRIVATE)
        list = prefs.getStringSet("blocked_links", mutableSetOf())?.toMutableList() ?: mutableListOf()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.adapter = adapter

        addBtn.setOnClickListener {
            val link = input.text.toString().trim()
            if (link.isNotEmpty() && !list.contains(link)) {
                list.add(link)
                saveList()
                adapter.notifyDataSetChanged()
                input.text.clear()
            }
        }

        clearBtn.setOnClickListener {
            list.clear()
            saveList()
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val removed = list.removeAt(position)
            saveList()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "تم حذف: $removed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveList() {
        val prefs = getSharedPreferences("BlockedPrefs", MODE_PRIVATE)
        prefs.edit().putStringSet("blocked_links", list.toSet()).apply()
    }
}
