package android.thaihn.roomandsqlitesample

import android.content.Intent
import android.os.Bundle
import android.thaihn.roomandsqlitesample.databinding.ActivityMainBinding
import android.thaihn.roomandsqlitesample.room.ui.RoomActivity
import android.thaihn.roomandsqlitesample.sqlite.ui.ContactControllerActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.btnSqlite.setOnClickListener {
            startActivity(Intent(this, ContactControllerActivity::class.java))
        }

        mainBinding.btnRoom.setOnClickListener {
            startActivity(Intent(this, RoomActivity::class.java))
        }
    }
}
