package edu.piedpiper.uw.ischool.watsonchat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase;

import kotlinx.android.synthetic.main.activity_message.*
import android.content.Intent
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DataSnapshot
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.widget.TextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener






class MessageActivity : AppCompatActivity() {

    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mMessageRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar)

//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//
//        if (mFirebaseUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        } else {
//
//        }

        val mFirebaseDatabase = FirebaseDatabase.getInstance().getReference()

        val query = FirebaseDatabase.getInstance()
                .getReference()
                .child("threads")
                .child("profile how-to")
                .child("chat")
                .limitToLast(50)

        val options = FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(query, Message::class.java)
                .build()

        val adapter = object : FirebaseRecyclerAdapter<Message, RecievedMessageViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecievedMessageViewHolder {
                Log.i("im here", "help")
                return RecievedMessageViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_message_received, parent, true))
            }

            protected override fun onBindViewHolder(holder: RecievedMessageViewHolder, position: Int, model: Message) {
                holder.bind(model)
            }
        }

        mLinearLayoutManager = LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mMessageRecyclerView = findViewById(R.id.reyclerview_message_list) as RecyclerView
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(adapter);
    }

    class RecievedMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var messengerTextView: TextView
        internal var messageBodyTextView: TextView
        internal var messageTimeTextView: TextView

        init {
            messengerTextView = itemView.findViewById(R.id.text_message_name) as TextView
            messageBodyTextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
            messageTimeTextView = itemView.findViewById<View>(R.id.text_message_time) as TextView
        }

        fun bind(message: Message) {
            messengerTextView.setText(message.userName)
            messageBodyTextView.setText(message.text)
            messageTimeTextView.setText(message.time.toString())
        }
    }

    private class SentMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var messengerTextView: TextView
        internal var messageBodyTextView: TextView
        internal var messageTimeTextView: TextView

        init {
            messengerTextView = itemView.findViewById(R.id.text_message_name) as TextView
            messageBodyTextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
            messageTimeTextView = itemView.findViewById<View>(R.id.text_message_time) as TextView
        }

        fun bind(message: Message) {
            messengerTextView.setText(message.userName)
            messageBodyTextView.setText(message.text)
            messageTimeTextView.setText(message.time.toString())
        }
    }
}
