package edu.piedpiper.uw.ischool.watsonchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import edu.piedpiper.uw.ischool.watsonchat.R.id.recyclerView_thread
import kotlinx.android.synthetic.main.activity_thread.*
import kotlinx.android.synthetic.main.thread_row.view.*

class ThreadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)
        Log.i("ThreadActivity", "onCreate ThreadActivity")
//        val button = findViewById<Button>(R.id.logout)
//        button.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(this, MainActivity::class.java))
//        }

        recyclerView_thread.layoutManager = LinearLayoutManager(this)
        recyclerView_thread.adapter = ThreadAdapter()
    }
}

class ThreadAdapter: RecyclerView.Adapter<CustomViewHolder>() {

    // number of items
    override fun getItemCount(): Int {
        return 9
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder{
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.thread_row, parent, false)
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //holder.view.contactName.text = 'insert name'
        //holder.view.lastText.text = 'insert text'
        //holder.view.timeStamp.text = 'insert time'
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}

