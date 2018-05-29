package edu.piedpiper.uw.ischool.watsonchat

import android.app.FragmentBreadCrumbs
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat.startActivity
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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_thread.*
import kotlinx.android.synthetic.main.thread_row.view.*
import java.text.DateFormat.getTimeInstance
import java.util.*

class ThreadActivity : AppCompatActivity() {

    private var mThreads: ArrayList<Thread>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)
        Log.i("ThreadActivity", "onCreate ThreadActivity")
//        val button = findViewById<Button>(R.id.logout)
//        button.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(this, MainActivity::class.java))
//        }

        mThreads = arrayListOf()

        val myAdapter = ThreadAdapter(mThreads!!)
        recyclerView_thread.layoutManager = LinearLayoutManager(this)
        recyclerView_thread.adapter = myAdapter


        val reference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/threads")



        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                //val model = dataSnapshot.getValue(Thread::class.java)

                Log.i("thread", dataSnapshot.child("timeCreated").toString())
                Log.i("thread", dataSnapshot.child("timeCreated").toString())

                val time = dataSnapshot.child("timeCreated").value as Long
                val name = dataSnapshot.child("threadName").value as String
                val id = dataSnapshot.key

                val thread = Thread(threadName = name, timeCreated = time, threadId = id)


                mThreads!!.add(thread);

                myAdapter.notifyDataSetChanged()
                //mMessageRecyclerView.smoothScrollToPosition(mChats!!.size -1);

                //Log.i("MessageActivity", "" + mChats!!.size )
                //Log.i("MessageActivity", "" + mMessageRecyclerView.childCount )
            }
        })


        val btn = findViewById<FloatingActionButton>(R.id.btn_action) as FloatingActionButton
        btn.setOnClickListener({

            var temp = mutableMapOf<Any, Any>();

            //var userName = FirebaseAuth.getInstance().currentUser!!.displayName
            //var userId = FirebaseAuth.getInstance().currentUser!!.uid

            temp.put("timeCreated", ServerValue.TIMESTAMP)
            temp.put("threadName", "Temp Thread Name")

            val key = reference.push().key
            reference.child(key).setValue(temp)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })
        })
    }

    private fun threadClicked(thread : Thread) {
        val messageIntent = Intent(this, ThreadActivity::class.java)
        messageIntent.putExtra("threadId", thread.threadId)
        startActivity(messageIntent)
    }
}

class ThreadAdapter(var threads:MutableList<Thread>): RecyclerView.Adapter<CustomViewHolder>() {

    // number of items
    override fun getItemCount(): Int {
        return threads.size
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
        val thread = threads.get(position)

        holder.bind(thread)
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    fun bind(thread: Thread) {
        val contactName = view.findViewById(R.id.contactName) as TextView
        val lastText = view.findViewById(R.id.lastText) as TextView
        val timeStamp = view.findViewById(R.id.timeStamp) as TextView

        view.setOnClickListener {
            val messageIntent = Intent(view.context, ThreadActivity::class.java)
            messageIntent.putExtra("threadId", thread.threadId)
            view.context.startActivity(messageIntent)
        }

        contactName.text = thread.threadName;
        lastText.text = "temp text";
        timeStamp.text = getTimeDate(thread.timeCreated!!)
    }

    private fun getTimeDate(timeStamp: Long): String {
        try {
            val dateFormat = getTimeInstance()
            val netDate = Date(timeStamp)
            return dateFormat.format(netDate)
        } catch (e: Exception) {
            return "date"
        }
    }
}

