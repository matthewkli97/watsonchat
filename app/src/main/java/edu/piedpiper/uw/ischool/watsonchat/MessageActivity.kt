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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.firebase.ui.database.SnapshotParser
import android.text.format.DateUtils.formatDateTime
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.*
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener




class MessageActivity : AppCompatActivity() {

    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mMessageRecyclerView: RecyclerView
    private var mMessageAdapter:MessageAdapter? = null
    private var mChats: ArrayList<Message>? = null
    private var message = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setSupportActionBar(toolbar)

        mChats = arrayListOf()
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

        //mLinearLayoutManager.findLastCompletelyVisibleItemPosition()

        val myAdapter = MyAdapter(mChats!!)
        mMessageRecyclerView = findViewById(R.id.reyclerview_message_list) as RecyclerView
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager)

        mMessageRecyclerView.adapter = myAdapter

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
                myAdapter.notifyDataSetChanged()
                mMessageRecyclerView.smoothScrollToPosition(mChats!!.size -1);

                Log.i("MessageActivity", "" + mChats!!.size )
                Log.i("MessageActivity", "" + mMessageRecyclerView.childCount )
            }
        })


        // Firebase/EditTextx
        val buttonSubmit = findViewById(R.id.button_chatbox_send) as Button
        val et_message = findViewById(R.id.edittext_chatbox) as EditText

        et_message.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                message = s.toString()

                if(message.length > 0) {
                    buttonSubmit.isEnabled = true
                } else {
                    buttonSubmit.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
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

            //var userName = FirebaseAuth.getInstance().currentUser!!.displayName
            //var userId = FirebaseAuth.getInstance().currentUser!!.uid

            var userName = "Billy!"
            var userId = "1"

            temp.put("userId", userId)
            temp.put("userName", userName!!)
            temp.put("time", ServerValue.TIMESTAMP)
            temp.put("text", message)


            val key = FirebaseDatabase.getInstance().getReference().child("general").push().key
            FirebaseDatabase.getInstance().getReference().child("general").child(key).setValue(temp)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })
            et_message.setText("")
            message = ""
        }
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
