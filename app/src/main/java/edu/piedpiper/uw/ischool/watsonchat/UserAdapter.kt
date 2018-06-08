package edu.piedpiper.uw.ischool.watsonchat

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.CompoundButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.content_message.view.*


class UserAdapter(context: Context, users: ArrayList<User>, var threadId:String) : ArrayAdapter<User>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val user = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        }
        // Lookup view for data population
        val tvName = convertView!!.findViewById(R.id.tv_userName) as TextView
        val checkBox = convertView!!.findViewById(R.id.checkBox) as CheckBox
        // Populate the data into the template view using the data object
        checkBox.setChecked(user.selected)
        tvName.text = user!!.uName

        checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val threadUserRef = FirebaseDatabase.getInstance().reference.child("threadRef").child(threadId).child("users").child(user!!.uid)
            if(isChecked) {
                threadUserRef.setValue(user.uName)
            } else {
                threadUserRef.setValue(null)
            }
        })

        // Return the completed view to render on screen
        return convertView
    }
}

private fun View.startActivity(intent: Intent) {

}
