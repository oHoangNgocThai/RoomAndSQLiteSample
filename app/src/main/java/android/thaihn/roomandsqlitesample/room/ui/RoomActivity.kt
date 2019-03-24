package android.thaihn.roomandsqlitesample.room.ui

import android.app.Activity
import android.os.Bundle
import android.thaihn.roomandsqlitesample.R
import android.thaihn.roomandsqlitesample.databinding.ActivityRoomBinding
import android.thaihn.roomandsqlitesample.room.database.AppRoomDatabase
import android.thaihn.roomandsqlitesample.room.entity.ContactEntity
import android.thaihn.roomandsqlitesample.sqlite.entity.Contact
import android.thaihn.roomandsqlitesample.sqlite.ui.ContactAdapter
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RoomActivity : AppCompatActivity(), ContactAdapter.ContactListener {

    companion object {
        private val TAG = RoomActivity::class.java.simpleName
    }

    private lateinit var roomBinding: ActivityRoomBinding

    private val mContactAdapter = ContactAdapter(arrayListOf(), this)

    private var mContactEdit: Contact? = null

    private var appRoomDatabase: AppRoomDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomBinding = DataBindingUtil.setContentView(this, R.layout.activity_room)
        appRoomDatabase = AppRoomDatabase.getAppDataBase(this)

        roomBinding.rvContact.apply {
            adapter = mContactAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        actionBar?.title = "Manage contact Room"


        roomBinding.btnAdd.setOnClickListener {
            addContact()
        }

        roomBinding.btnDelete.setOnClickListener {
            deleteContactByAddress()
        }

        roomBinding.btnEdit.setOnClickListener {
            updateContact()
        }

        appRoomDatabase?.getContactDao()
            ?.getAllUser()?.observe(this,
                Observer<List<Contact>> { t ->
                    Log.d(TAG, "List contact:$t")
                    mContactAdapter.addAllContact(t)
                })
    }

    private fun updateContact() {
        mContactEdit?.let {
            val name = roomBinding.edtName.text.trim().toString()
            val phone = roomBinding.edtPhone.text.trim().toString()
            val address = roomBinding.edtAddress.text.trim().toString()

            if (name.isEmpty() && phone.isEmpty() && address.isEmpty()) {
                Toast.makeText(applicationContext, "Unless have one field name", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val newContact = ContactEntity(name, phone, address).apply {
                id = it.id
            }

            appRoomDatabase?.getContactDao()
                ?.updateUser(newContact)

            mContactEdit = null
            releaseInput()
            hideKeyboard()
        }
    }

    private fun deleteContactByAddress() {
        val address = roomBinding.edtAddress.text.trim().toString()

        if (address.isEmpty()) {
            Toast.makeText(applicationContext, "Address is empty!", Toast.LENGTH_SHORT).show()
            return
        }
        appRoomDatabase?.getContactDao()
            ?.deleteUserByAddress(address)

    }

    override fun deleteContact(item: Contact) {
        val contactEntity = ContactEntity(item.name, item.phone, item.address)
        contactEntity.id = item.id
        appRoomDatabase?.getContactDao()
            ?.deleteUser(contactEntity)
    }

    override fun editContact(item: Contact) {
        mContactEdit = item
        roomBinding.edtName.setText(item.name)
        roomBinding.edtPhone.setText(item.phone)
        roomBinding.edtAddress.setText(item.address)
    }

    private fun addContact() {
        val name = roomBinding.edtName.text.trim().toString()
        val phone = roomBinding.edtPhone.text.trim().toString()
        val address = roomBinding.edtAddress.text.trim().toString()

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

        // Add contact using Room
        val contactEntity = ContactEntity(name, phone, address)
        val id = appRoomDatabase?.getContactDao()?.insertUser(contactEntity)
        Log.d(TAG, "addContact: id:$id")

        releaseInput()
        hideKeyboard()
        // update all list
    }

    private fun releaseInput() {
        roomBinding.edtName.setText("")
        roomBinding.edtAddress.setText("")
        roomBinding.edtPhone.setText("")
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
