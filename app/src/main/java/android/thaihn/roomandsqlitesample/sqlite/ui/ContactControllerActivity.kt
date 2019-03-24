package android.thaihn.roomandsqlitesample.sqlite.ui

import android.os.Bundle
import android.thaihn.roomandsqlitesample.R
import android.thaihn.roomandsqlitesample.databinding.ActivityContactControllerBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

class ContactControllerActivity : AppCompatActivity() {

    private lateinit var contactBinding: ActivityContactControllerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_controller)
    }
}
