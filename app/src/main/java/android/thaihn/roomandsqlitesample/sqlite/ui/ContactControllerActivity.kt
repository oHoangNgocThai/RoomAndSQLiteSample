package android.thaihn.roomandsqlitesample.sqlite.ui

import android.app.Activity
import android.os.Bundle
import android.thaihn.roomandsqlitesample.R
import android.thaihn.roomandsqlitesample.databinding.ActivityContactControllerBinding
import android.thaihn.roomandsqlitesample.sqlite.controller.ContactController
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ContactControllerActivity : AppCompatActivity(), ContactAdapter.ContactListener {

    companion object {
        private val TAG = ContactControllerActivity::class.java.simpleName
    }

    private var mContactEdit: Contact? = null

    private val mDbController = ContactController(this)

    private val mContactAdapter = ContactAdapter(arrayListOf(), this)

    private lateinit var contactBinding: ActivityContactControllerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_controller)

        contactBinding.rvContact.apply {
            adapter = mContactAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        actionBar?.title = "Manage contact SQLite"

        updateListContact()

        contactBinding.btnAdd.setOnClickListener {
            addContact()
        }

        contactBinding.btnDelete.setOnClickListener {
            deleteContactByAddress()
        }

        contactBinding.btnEdit.setOnClickListener {
            updateContact()
        }
    }

    override fun deleteContact(item: Contact) {
        deleteContact(item.id)
    }

    override fun editContact(item: Contact) {
        mContactEdit = item
        contactBinding.edtName.setText(item.name)
        contactBinding.edtPhone.setText(item.phone)
        contactBinding.edtAddress.setText(item.address)
    }

    private fun updateContact() {
        mContactEdit?.let { contact ->
            val name = contactBinding.edtName.text.trim().toString()
            val phone = contactBinding.edtPhone.text.trim().toString()
            val address = contactBinding.edtAddress.text.trim().toString()

            if (name.isEmpty() && phone.isEmpty() && address.isEmpty()) {
                Toast.makeText(applicationContext, "Unless have one field name", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            val newContact = Contact(0, name, phone, address)

            val id = mDbController.update(contact, newContact)

            Log.d(TAG, "deleteContact: id$id")
            if (id > 0) {
                Toast.makeText(applicationContext, "Update contact success", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, "Update contact fail", Toast.LENGTH_SHORT).show()
            }

            mContactEdit = null
            releaseInput()
            hideKeyboard()
            updateListContact()
        }
    }

    private fun deleteContactByAddress() {
        val address = contactBinding.edtAddress.text.trim().toString()

        if (address.isEmpty()) {
            Toast.makeText(applicationContext, "Address is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val numId = mDbController.delete(address)
        Log.d(TAG, "deleteContact: id$numId")
        if (numId > 0) {
            Toast.makeText(
                applicationContext,
                "Delete contact by address success",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(applicationContext, "Delete contact by address fail", Toast.LENGTH_SHORT)
                .show()
        }

        releaseInput()
        hideKeyboard()
        updateListContact()
    }

    private fun deleteContact(_id: Long) {
        val id = mDbController.delete(_id)
        Log.d(TAG, "deleteContact: id$id")

        if (id > 0) {
            Toast.makeText(applicationContext, "Delete contact success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Delete contact fail", Toast.LENGTH_SHORT).show()
        }

        updateListContact()
    }

    private fun addContact() {
        val name = contactBinding.edtName.text.trim().toString()
        val phone = contactBinding.edtPhone.text.trim().toString()
        val address = contactBinding.edtAddress.text.trim().toString()

        if (name.isEmpty()) {
            Toast.makeText(applicationContext, "Name is empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.isEmpty()) {
            Toast.makeText(applicationContext, "Phone is empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (address.isEmpty()) {
            Toast.makeText(applicationContext, "Address is empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val contact = Contact(0, name, phone, address)
        val id = mDbController.insert(contact)

        Log.d(TAG, "addContact: id:$id")
        if (id.toInt() > 0) {
            Toast.makeText(applicationContext, "Add contact success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Add contact fail", Toast.LENGTH_SHORT).show()
        }

        releaseInput()
        hideKeyboard()
        // update all list
        updateListContact()
    }

    private fun releaseInput() {
        contactBinding.edtName.setText("")
        contactBinding.edtAddress.setText("")
        contactBinding.edtPhone.setText("")
    }

    private fun updateListContact() {
        val contacts = mDbController.getAllContact()
        mContactAdapter.addAllContact(contacts)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
