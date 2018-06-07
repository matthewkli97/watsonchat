package edu.piedpiper.uw.ischool.watsonchat


import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_thread.*
import java.text.DateFormat.getTimeInstance
import java.util.*
import kotlin.collections.HashMap

class ThreadActivity : AppCompatActivity() {
    lateinit var mFirebaseAuth: FirebaseAuth
    var mFirebaseUser: FirebaseUser? = null
    private var mThreads: ArrayList<Thread>? = null
    private var mThreadMap:HashMap<String,Int>? = null
    private var connectionReciever: BroadcastReceiver? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectionReciever)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver( connectionReciever, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    // Code adapted from: https://medium.com/@101/android-toolbar-for-appcompatactivity-671b1d10f354
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.action_profile -> {
                val settingIntent = Intent(this, ProfileActivity::class.java)
                //settingIntent.putExtra("threadId", threadId)
                //settingIntent.putExtra("threadName", threadName)
                startActivity(settingIntent)
                return true
            }
            R.id.sign_out -> {
                val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                builder = AlertDialog.Builder(this)
            }
            builder.setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this, MainActivity::class.java))
                    })
                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
               return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread)

        connectionReciever = object : BroadcastReceiver() {
            val current:Boolean? = null

            override fun onReceive(context: Context, intent: Intent) {
                val dis = findViewById(R.id.disconnected) as TextView
                if(!isOnline(context)) {
                    displayAlert(context)
                    dis.visibility = View.VISIBLE
                } else {
                    dis.visibility = View.INVISIBLE
                }
            }

            fun isOnline(context: Context): Boolean {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                //should check null because in airplane mode it will be null
                return netInfo != null && netInfo.isConnected
            }

            fun displayAlert(context: Context) {
                val builder = AlertDialog.Builder(context)
                builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    context.startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
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
        }

        registerReceiver( connectionReciever, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Preliminary check to ensure login user
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        mThreads = arrayListOf()
        mThreadMap = hashMapOf()

        val myAdapter = ThreadAdapter(mThreads!!)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView_thread.layoutManager = layoutManager
        recyclerView_thread.adapter = myAdapter

        val reference: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/threadRef")
        val referenceQuery: Query = reference.orderByChild("lastMessageTime")

        referenceQuery.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                val time = dataSnapshot.child("lastMessageTime").value as Long
                val name = dataSnapshot.child("threadName").value as String
                val lastMessage = dataSnapshot.child("lastMessageText").value as String
                val id = dataSnapshot.key

                val thread = Thread(threadName = name, lastMessageTime = time, threadId = id, lastMessageText = lastMessage)
                val currIndex = mThreadMap!!.get(id)

                var valid = false
                dataSnapshot.child("users").children.forEach({
                    if(it.key.equals(mFirebaseUser!!.uid)) {
                        valid = true
                    }
                })

                if(valid) {
                    if(currIndex == null) {
                        mThreads!!.add(thread);
                        mThreadMap!!.put(id, mThreads!!.size - 1)
                    } else {
                        mThreads!!.removeAt(currIndex)
                        mThreadMap!!.remove(id)
                        mThreads!!.add(thread);
                        mThreadMap!!.put(id, mThreads!!.size - 1)
                    }
                    myAdapter.notifyDataSetChanged()
                } else {
                    if(currIndex != null) {
                        mThreads!!.removeAt(currIndex)
                        mThreadMap!!.remove(id)
                        myAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val time = dataSnapshot.child("lastMessageTime").value as Long
                val name = dataSnapshot.child("threadName").value as String
                val lastMessage = dataSnapshot.child("lastMessageText").value as String
                val id = dataSnapshot.key
                val thread = Thread(threadName = name, lastMessageTime = time, threadId = id, lastMessageText = lastMessage)

                var valid = false
                dataSnapshot.child("users").children.forEach({
                    if(it.key.equals(mFirebaseUser!!.uid)) {
                        valid = true
                    }
                })

                if(valid) {
                    mThreads!!.add(thread);
                    mThreadMap!!.put(id, mThreads!!.size - 1)
                    myAdapter.notifyDataSetChanged()
                }
            }
        })

        val btn = findViewById(R.id.btn_action) as FloatingActionButton
        btn.setOnClickListener({

            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val userName = FirebaseAuth.getInstance().currentUser!!.displayName as String
            var threadRefObj = mutableMapOf<Any, Any>();
            var userObj = mutableMapOf<Any, Any>();

            threadRefObj.put("lastMessageTime", ServerValue.TIMESTAMP)
            threadRefObj.put("threadName", "New Thread")
            threadRefObj.put("lastMessageText", " ")
            userObj.put(userId, userName)
            threadRefObj.put("users", userObj)

            // REFERS TO THREADREF.key
            val key = reference.push().key

            reference.child(key).setValue(threadRefObj)
                    .addOnSuccessListener(OnSuccessListener<Void> {
                        Log.i("MessageActivity", "Success")

//                        val messageIntent = Intent(this, MessageActivity::class.java)
//                        messageIntent.putExtra("threadId", key)
//                        messageIntent.putExtra("threadName", "New Thread")
//                        startActivity(messageIntent)

                        val settingIntent = Intent(this, ThreadSettingActivity::class.java)
                        settingIntent.putExtra("threadId", key)
                        settingIntent.putExtra("threadName", "New Thread")
                        settingIntent.putExtra("new",true)
                        startActivity(settingIntent)
                    })
                    .addOnFailureListener(OnFailureListener {
                        Log.i("MessageActivity", "Failure")
                    })
        })
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


//    override fun onBackPressed() {
//            AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
//                    .setMessage("Are you sure you want to exit?")
//                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
//                        finish()
//                        System.exit(0)
//                    }).setNegativeButton("No", null).show()
//        }
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
            messageIntent.putExtra("threadName", thread.threadName)
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

