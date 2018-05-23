package edu.piedpiper.uw.ischool.watsonchat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter(var context:Context, var messages:MutableList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2


    override fun getItemViewType(position: Int): Int {
        val message = messages.get(position) as Message

        return if (message.userId.equals(FirebaseAuth.getInstance().uid)) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        System.out.println("asdfas")
        if (viewType === VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
            return SentMessageViewHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
            return RecievedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages.get(position)

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageViewHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as RecievedMessageViewHolder).bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}

private class RecievedMessageViewHolder(v: View) :  RecyclerView.ViewHolder(v) {
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
    }
}
