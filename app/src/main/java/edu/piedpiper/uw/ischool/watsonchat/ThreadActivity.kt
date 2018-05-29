package edu.piedpiper.uw.ischool.watsonchat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
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


        val reference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/threads")


        val btn = findViewById<FloatingActionButton>(R.id.btn_action) as FloatingActionButton
        btn.setOnClickListener({

            var temp = mutableMapOf<Any, Any>();

            //var userName = FirebaseAuth.getInstance().currentUser!!.displayName
            //var userId = FirebaseAuth.getInstance().currentUser!!.uid

            temp.put("timeCreated", ServerValue.TIMESTAMP)
            temp.put("userIds", listOf(FirebaseAuth.getInstance().uid))
            temp.put("userNames", listOf(FirebaseAuth.getInstance().currentUser!!.displayName))

            val key = reference.push().key
            reference.child(key).setValue(temp)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })
        })

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
        cellForRow.setOnClickListener({
            //go to messages
            println("Lets see our messages")
        });
        return CustomViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val contactName = holder.view.findViewById(R.id.contactName) as TextView
        val lastText = holder.view.findViewById(R.id.lastText) as TextView
        val timeStamp = holder.view.findViewById(R.id.timeStamp) as TextView


        contactName.text = "hello world";
        lastText.text = "hello";

        timeStamp.text = "blah";

        //holder.view.lastText.text = 'insert text'
        //holder.view.timeStamp.text = 'insert time'
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}

