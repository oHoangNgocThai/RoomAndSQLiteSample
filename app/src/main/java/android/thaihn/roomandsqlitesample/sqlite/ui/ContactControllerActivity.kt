package android.thaihn.roomandsqlitesample.sqlite.ui

import android.os.Bundle
import android.thaihn.roomandsqlitesample.R
import android.thaihn.roomandsqlitesample.databinding.ActivityContactControllerBinding
import android.thaihn.roomandsqlitesample.sqlite.controller.ContactController
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactControllerActivity : AppCompatActivity(), ContactAdapter.ContactListener {

    companion object {
        private val TAG = ContactControllerActivity::class.java.simpleName
    }

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

        updateListContact()

        contactBinding.btnAdd.setOnClickListener {
            addContact()
        }
    }

    override fun deleteContact(item: Contact) {
        deleteContact(item.id)
    }

    override fun editContact(item: Contact) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun deleteContact(id: Long) {
        val id = mDbController.delete(id)
        Log.d(TAG, "deleteContact: id$id")

        if (id.toInt() > 0) {
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
}
