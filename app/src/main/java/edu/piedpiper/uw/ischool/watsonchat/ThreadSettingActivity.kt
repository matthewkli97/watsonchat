package edu.piedpiper.uw.ischool.watsonchat

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_thread_setting.*

class ThreadSettingActivity : AppCompatActivity() {

    var threadId:String? = null
    var threadName:String? = null
    var etThreadName:String? = null


    var userRef:DatabaseReference? = null
    var userListener:ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread_setting)
        setSupportActionBar(toolbar)

        threadId = intent.getStringExtra("threadId")
        threadName = intent.getStringExtra("threadName")
        etThreadName = threadName

        val arrayOfUsers = ArrayList<User>()
        // Create the adapter to convert the array to views
        val adapter = UserAdapter(this, arrayOfUsers, threadId!!)
        // Attach the adapter to a ListView
        val listView = findViewById(R.id.listViewUsers) as ListView
        listView.setAdapter(adapter)

        val threadUserRef = FirebaseDatabase.getInstance().reference.child("threadRef").child(threadId).child("users")
        var threadUsers = ArrayList<String>()
        threadUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(child:DataSnapshot in dataSnapshot.children) {
                    threadUsers.add(child.key)
                }

                Log.i("asdf", dataSnapshot.value.toString())
            }
            override fun onCancelled(p0: DatabaseError?) {}
        })


        //val userRef = FirebaseDatabase.getInstance().reference.child("threadRef").child(threadId).child("users")
        userRef = FirebaseDatabase.getInstance().reference.child("users")
        userListener = userRef!!.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot?) {}
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val uid = dataSnapshot.key
                val uName = dataSnapshot.value as String
                val newUser = User(uid, uName, threadUsers.contains(uid))
                adapter.add(newUser)
            }
        })

        val et_name = findViewById(R.id.te_threadName) as EditText
        et_name.setText(etThreadName)

        val buttonSubmit = findViewById(R.id.btn_save) as Button
        buttonSubmit.isEnabled = false

        buttonSubmit.setOnClickListener({
            val nameRef = FirebaseDatabase.getInstance().reference.child("threadRef").child(threadId).child("threadName")

            nameRef.setValue(etThreadName)
            threadName = etThreadName
            buttonSubmit.isEnabled = false
        })

        et_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                etThreadName = s.toString()

                buttonSubmit.isEnabled = (!etThreadName.equals(threadName) && etThreadName!!.length > 0)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    override fun onStop() {
        super.onStop()
        userRef!!.removeEventListener(userListener)
    }

}