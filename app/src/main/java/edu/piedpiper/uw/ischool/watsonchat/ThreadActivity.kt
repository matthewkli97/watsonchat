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
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_thread.*
import java.text.DateFormat.getTimeInstance
import java.util.*

class ThreadActivity : AppCompatActivity() {

    private var mThreads: ArrayList<Thread>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        mThreads = arrayListOf()

        var mThreadMap = hashMapOf<String, Int>()

        val myAdapter = ThreadAdapter(mThreads!!)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView_thread.layoutManager = layoutManager
        recyclerView_thread.adapter = myAdapter

        val reference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/threadRef")


        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val time = dataSnapshot.child("lastMessageTime").value as Long
                val name = dataSnapshot.child("threadName").value as String
                val lastMessage = dataSnapshot.child("lastMessageText").value as String
                val id = dataSnapshot.key

                val thread = Thread(threadName = name, lastMessageTime = time, threadId = id, lastMessageText = lastMessage)
                val currIndex = mThreadMap.get(id)

                mThreads!!.set(currIndex!!, thread)
                myAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val time = dataSnapshot.child("lastMessageTime").value as Long
                val name = dataSnapshot.child("threadName").value as String
                val lastMessage = dataSnapshot.child("lastMessageText").value as String
                val id = dataSnapshot.key

                val thread = Thread(threadName = name, lastMessageTime = time, threadId = id, lastMessageText = lastMessage)

                mThreads!!.add(thread);
                mThreadMap.put(id, mThreads!!.size - 1)

                myAdapter.notifyDataSetChanged()
            }
        })

        val btn = findViewById<FloatingActionButton>(R.id.btn_action) as FloatingActionButton
        btn.setOnClickListener({
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userName = FirebaseAuth.getInstance().currentUser!!.displayName as String
            var threadRefObj = mutableMapOf<Any, Any>();
            var userObj = mutableMapOf<Any, Any>();

            threadRefObj.put("lastMessageTime", ServerValue.TIMESTAMP)
            threadRefObj.put("threadName", "New Thread")
            threadRefObj.put("lastMessageText", " ")


            // REFERS TO THREADREF.key
            val key = reference.push().key

            reference.child(key).setValue(threadRefObj)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })

            // Build new Thread
            val userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId).child("threads")

            userRef.child(key).setValue(true)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success add user reference to thread")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })

            userObj.put(userId, userName)
            // Add user to thread
            val thread = FirebaseDatabase.getInstance().reference.child("threads")
            thread.child(key).child("users").setValue(userObj)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")

                        val messageIntent = Intent(this, MessageActivity::class.java)
                        messageIntent.putExtra("threadId", key)
                        startActivity(messageIntent)
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

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionExit()
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
            val messageIntent = Intent(view.context, MessageActivity::class.java)
            messageIntent.putExtra("threadId", thread.threadId)
            view.context.startActivity(messageIntent)
        }

        contactName.text = thread.threadName;
        lastText.text = thread.lastMessageText;
        timeStamp.text = getTimeDate(thread.lastMessageTime!!)
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

