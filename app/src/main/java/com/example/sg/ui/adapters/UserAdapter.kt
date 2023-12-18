package com.example.sg.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sg.R
import com.example.sg.data.models.User
import com.example.sg.databinding.ItemUserBinding

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            with(binding) {
                textName.text = user.name
                textEmail.text = context.getString(R.string.email_format, user.email)
                textGender.text = context.getString(R.string.gender_format, user.gender)
                textStatus.text = context.getString(R.string.status_format, user.status)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}