package edu.piedpiper.uw.ischool.watsonchat

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import kotlinx.android.synthetic.main.activity_message.*
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import android.widget.TextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.firebase.ui.database.SnapshotParser
import android.text.format.DateUtils.formatDateTime
import com.google.firebase.database.*


class MessageActivity : AppCompatActivity() {

    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mMessageRecyclerView: RecyclerView
    private var mMessageAdapter:MessageAdapter? = null
    private var mChats: MutableList<Message>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar)

        mChats = mutableListOf()
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

        mLinearLayoutManager = LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageAdapter = MessageAdapter(this, mChats!!)
        mMessageRecyclerView = findViewById(R.id.reyclerview_message_list) as RecyclerView
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager)

        val query:DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/general")


        query.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val model = dataSnapshot.getValue(Message::class.java)
                mChats!!.add(model!!)
                mMessageAdapter!!.notifyDataSetChanged()
                mMessageRecyclerView.setAdapter(mMessageAdapter)
            }
        })

    }
}




//
//
//class MessageListAdapter(private val mContext: Context, private val mMessageList: List<Message>) : RecyclerView.Adapter<ViewHolder>() {
//
//    override fun getItemCount(): Int {
//        return mMessageList.size
//    }
//
//    // Determines the appropriate ViewType according to the sender of the message.
//    override fun getItemViewType(position: Int): Int {
//        val message = mMessageList[position] as Message
//
//        return if (message.userId.equals(FirebaseAuth.getInstance().currentUser)) {
//            // If the current user is the sender of the message
//            VIEW_TYPE_MESSAGE_SENT
//        } else {
//            // If some other user sent the message
//            VIEW_TYPE_MESSAGE_RECEIVED
//        }
//    }
//
//    // Inflates the appropriate layout according to the ViewType.
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view: View
//
//        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
//            view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_message_sent, parent, false)
//            return SentMessageViewHolder(view)
//        } else {
//            view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_message_received, parent, false)
//            return RecievedMessageViewHolder(view)
//        }
//    }
//
//    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = mMessageList[position] as Message
//
//        when (holder.itemViewType) {
//            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageViewHolder).bind(message)
//            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as RecievedMessageViewHolder).bind(message)
//        }
//    }
//
//    private inner class RecievedMessageViewHolder(v: View) : ViewHolder {
//        internal var messengerTextView: TextView
//        internal var messageBodyTextView: TextView
//        internal var messageTimeTextView: TextView
//
//        init {
//            messengerTextView = itemView.findViewById(R.id.text_message_name) as TextView
//            messageBodyTextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
//            messageTimeTextView = itemView.findViewById<View>(R.id.text_message_time) as TextView
//        }
//
//        fun bind(message: Message) {
//            messengerTextView.setText(message.userName)
//            messageBodyTextView.setText(message.text)
//
//        }
//    }
//
//    private inner class SentMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        internal var messengerTextView: TextView
//        internal var messageBodyTextView: TextView
//        internal var messageTimeTextView: TextView
//
//        init {
//            messengerTextView = itemView.findViewById(R.id.text_message_name) as TextView
//            messageBodyTextView = itemView.findViewById<View>(R.id.text_message_body) as TextView
//            messageTimeTextView = itemView.findViewById<View>(R.id.text_message_time) as TextView
//        }
//
//        fun bind(message: Message) {
//            messengerTextView.setText(message.userName)
//            messageBodyTextView.setText(message.text)
//        }
//    }
//
//    companion object {
//        private val VIEW_TYPE_MESSAGE_SENT = 1
//        private val VIEW_TYPE_MESSAGE_RECEIVED = 2
//    }
