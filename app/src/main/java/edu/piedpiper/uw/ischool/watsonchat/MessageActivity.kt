package edu.piedpiper.uw.ischool.watsonchat

import android.content.Context
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog


class MessageActivity : AppCompatActivity() {

    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mMessageRecyclerView: RecyclerView
    private var mChats: ArrayList<Message>? = null
    private var message = ""
    private var threadId:String? = null
    private var threadName:String? = null
    private var chatListener:ChildEventListener? = null
    private var query:DatabaseReference? = null

    private var chatNameListener:ValueEventListener? = null
    private var chatNameRef:DatabaseReference? = null


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

        threadId = intent.getStringExtra("threadId")
        threadName = intent.getStringExtra("threadName")
        this.setTitle(threadName);

        mLinearLayoutManager = LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        // Prep recycler adapter
        val myAdapter = MessageAdapter(mChats!!)
        mMessageRecyclerView = findViewById(R.id.reyclerview_message_list) as RecyclerView
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager)
        mMessageRecyclerView.adapter = myAdapter

        // Prep database reference for querying of firebase database
        query = FirebaseDatabase.getInstance().reference.child("threads").child(threadId).child("chats")
        chatListener = query!!.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val model = dataSnapshot.getValue(Message::class.java)
                mChats!!.add(model!!)
                myAdapter.notifyDataSetChanged()
                mMessageRecyclerView.smoothScrollToPosition(mChats!!.size - 1)
            }
        })

        // Prep database reference for querying of firebase database
        chatNameRef = FirebaseDatabase.getInstance().reference.child("threadRef").child(threadId).child("threadName")
        chatNameListener = chatNameRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                setTitle(p0!!.value.toString())
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        mMessageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                mMessageRecyclerView.postDelayed(Runnable { mMessageRecyclerView.scrollToPosition(mChats!!.size - 1) }, 100)
            }
        })

        buttonSubmit.isEnabled = false
        buttonSubmit.setOnClickListener { view ->
            if(isOnline(this)) {
                var temp = mutableMapOf<Any, Any>();
                var userName = FirebaseAuth.getInstance().currentUser!!.displayName
                var userId = FirebaseAuth.getInstance().currentUser!!.uid

                temp.put("userId", userId)
                temp.put("userName", userName!!)
                temp.put("time", ServerValue.TIMESTAMP)
                temp.put("text", message)

                val key = FirebaseDatabase.getInstance().getReference().child("threads").child(threadId).child("chats").push().key
                FirebaseDatabase.getInstance().getReference().child("threads").child(threadId).child("chats").child(key).setValue(temp)
                        .addOnSuccessListener(OnSuccessListener<Void> {
                            Log.i("MessageActivity", "Success")
                        })
                        .addOnFailureListener(OnFailureListener {
                            Log.i("MessageActivity", "Failure")
                        })

                mMessageRecyclerView.postDelayed(Runnable { mMessageRecyclerView.scrollToPosition(mChats!!.size - 1) }, 100)

                var threadMap = mutableMapOf<String, Any>();
                threadMap.put("lastMessageTime", ServerValue.TIMESTAMP)
                threadMap.put("lastMessageText", userName!! + ": " + message)

                FirebaseDatabase.getInstance().getReference().child("threadRef").child(threadId).updateChildren(threadMap)
                        .addOnSuccessListener(OnSuccessListener<Void> {
                            Log.i("MessageActivity", "Success to update thread latest message")
                        })
                        .addOnFailureListener(OnFailureListener {
                            Log.i("MessageActivity", "Failure to update thread latest message")
                        })

                val messageRef = FirebaseDatabase.getInstance().reference.child("userMessages").child(userId).child("chat")
                val messageKey = messageRef.push().key

                messageRef.child(messageKey).setValue(message)
                        .addOnSuccessListener(OnSuccessListener<Void> {
                            Log.i("MessageActivity", "Success to update thread latest message")
                        })
                        .addOnFailureListener(OnFailureListener {
                            Log.i("MessageActivity", "Failure to update thread latest message")
                        })

                et_message.setText("")
                message = ""
            } else {
                displayAlert()
            }
        }

        val messages = findViewById(R.id.reyclerview_message_list) as RecyclerView
        val tester = messages.adapter.toString()
        Log.i("Debug", "Debugging101")
        Log.i("Debug", "KEVIN " + tester)
        print(tester)
    }

    private fun displayAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
            startActivityForResult(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), 0)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })
        builder.setMessage("You are not connected to the Internet. Airplane mode may be on, your wifi could be off" +
                " or you may not have service. Would you like to go to settings now to try to fix this?")
                .setTitle("Connectivity Issues")

        val dialog = builder.create()
        dialog.show()
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        //should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Code adapted from: https://medium.com/@101/android-toolbar-for-appcompatactivity-671b1d10f354
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.Menu -> {
                val settingIntent = Intent(this, ThreadSettingActivity::class.java)
                settingIntent.putExtra("threadId", threadId)
                settingIntent.putExtra("threadName", threadName)
                startActivity(settingIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_to_left, R.anim.slide_from_right)
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

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }
}