package com.bsktech.shopkeeper

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bsktech.shopkeeper.adaptors.StoreListAdaptor
import com.bsktech.shopkeeper.models.MainMenu
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), (MainMenu) -> Unit {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        val mainMenu = ArrayList<MainMenu>()
        mainMenu.add(MainMenu("Products", "Add, Update & Delete Products"))
        mainMenu.add(MainMenu("Orders", "Order & Payment Details"))

        recycler_view_main_menu.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = StoreListAdaptor(mainMenu, this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                signOut()
            }
            R.id.action_profile -> {
                startActivity(Intent(this, MyProfileActivity::class.java))
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun signOut() {
        // Firebase sign out
        auth.signOut()
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    override fun invoke(mainMenu: MainMenu) {
        if (mainMenu.title.equals("Products")){
            startActivity(Intent(this, MyProductsActivity::class.java))
        }
        if (mainMenu.title.equals("Orders")){
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }
    }
}
