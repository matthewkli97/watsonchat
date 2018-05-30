package edu.piedpiper.uw.ischool.watsonchat

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_message.*
import android.widget.TextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener


class MessageActivity : AppCompatActivity() {

    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mMessageRecyclerView: RecyclerView
    private var mChats: ArrayList<Message>? = null
    private var message = ""
    private var thread:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar)

        mChats = arrayListOf()
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Preliminary check to ensure login user
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        thread = intent.getStringExtra("threadId")

        mLinearLayoutManager = LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);


        // Prep recycler adapter
        val myAdapter = MessageAdapter(mChats!!)
        mMessageRecyclerView = findViewById(R.id.reyclerview_message_list) as RecyclerView
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager)
        mMessageRecyclerView.adapter = myAdapter

        // Prep database reference for querying of firebase database
        val query:DatabaseReference = FirebaseDatabase.getInstance().reference.child("threads").child(thread).child("chats")
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val model = dataSnapshot.getValue(Message::class.java)
                mChats!!.add(model!!)
                myAdapter.notifyDataSetChanged()
                mMessageRecyclerView.smoothScrollToPosition(mChats!!.size -1);
            }
        })

        // Scroll down option --> user scrolls up
        val scrollDown = findViewById(R.id.text_scroll) as TextView
        scrollDown.visibility = View.INVISIBLE

        scrollDown.setOnClickListener { view ->
            scrollDown.visibility = View.INVISIBLE
            mMessageRecyclerView.postDelayed(Runnable { mMessageRecyclerView.scrollToPosition(mChats!!.size -1) }, 100)
        }

        // Scroll listener for showing scroll button option
        mMessageRecyclerView
                .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)

                        //Log.i("track", "" + mLinearLayoutManager.findLastCompletelyVisibleItemPosition())
                        if(mLinearLayoutManager.findLastCompletelyVisibleItemPosition() <= mChats!!.size - 5) {
                            scrollDown.visibility = View.VISIBLE
                        } else {
                            scrollDown.visibility = View.INVISIBLE
                        }
                    }
                })


        // Firebase/EditText
        val buttonSubmit = findViewById(R.id.button_chatbox_send) as Button
        val et_message = findViewById(R.id.edittext_chatbox) as EditText

        et_message.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                message = s.toString()

                buttonSubmit.isEnabled = message.length > 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        et_message.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                //make the view scroll down to the bottom
                mMessageRecyclerView.postDelayed(Runnable { mMessageRecyclerView.scrollToPosition(mChats!!.size -1) }, 100)
            }
        })

        buttonSubmit.isEnabled = false
        buttonSubmit.setOnClickListener { view ->

            var temp = mutableMapOf<Any, Any>();

            var userName = FirebaseAuth.getInstance().currentUser!!.displayName
            var userId = FirebaseAuth.getInstance().currentUser!!.uid


            temp.put("userId", userId)
            temp.put("userName", userName!!)
            temp.put("time", ServerValue.TIMESTAMP)
            temp.put("text", message)


            val key = FirebaseDatabase.getInstance().getReference().child("threads").child(thread).child("chats").push().key
            FirebaseDatabase.getInstance().getReference().child("threads").child(thread).child("chats").child(key).setValue(temp)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })

            mMessageRecyclerView.postDelayed(Runnable { mMessageRecyclerView.scrollToPosition(mChats!!.size -1) }, 100)


            var threadMap = mutableMapOf<String, Any>();
            threadMap.put("lastMessageTime", ServerValue.TIMESTAMP)
            threadMap.put("lastMessageText", userName!! + " : " + message)

            FirebaseDatabase.getInstance().getReference().child("threads").child(thread).updateChildren(threadMap)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success to update thread latest message")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure to update thread latest message")
                    })

            et_message.setText("")
            message = ""
        }
    }
}