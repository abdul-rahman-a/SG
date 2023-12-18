package com.example.sg.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.sg.SharedViewModel
import com.example.sg.data.APIResult
import com.example.sg.data.models.User
import com.example.sg.databinding.FragmentUserBinding
import com.example.sg.dialogs.CreateUserDialog
import com.example.sg.ui.adapters.UserAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedViewModel by activityViewModels()

    private var progressBar: ProgressBar? = null
    private lateinit var userAdapter: UserAdapter
    private var isFabClickable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        clickListener()
        liveDataObserver()
    }

    private fun clickListener() {
        binding.fabAddUser.setOnClickListener {
            if (isFabClickable) {
                isFabClickable = false

                val dialog = CreateUserDialog {
                    createUser(it)
                    isFabClickable = true
                }

                dialog.show(childFragmentManager, "CreateUserDialog")
            }
        }
    }

    private fun createUser(user: User) {
        lifecycleScope.launch {
            viewModel.createUser(user)
        }
    }

    private fun liveDataObserver() {
        viewModel.createUser.observe(viewLifecycleOwner) {
            when(it.status) {
                APIResult.Status.SUCCESS -> {
                    hideLoadingIndicator()
                    showToast("User created successfully!")

                    lifecycleScope.launch {
                        viewModel.getUserList()
                    }
                }
                APIResult.Status.LOADING -> { showLoadingIndicator() }
                APIResult.Status.ERROR -> {
                    hideLoadingIndicator()
                    showToast(it.message.toString())
                }
            }
        }

        viewModel.users.observe(viewLifecycleOwner) {
            when(it.status) {
                APIResult.Status.SUCCESS -> {
                    userAdapter = UserAdapter(it.data!!)
                    binding.recyclerView.adapter = userAdapter
                    hideLoadingIndicator()
                }
                APIResult.Status.LOADING -> { showLoadingIndicator() }
                APIResult.Status.ERROR -> {
                    hideLoadingIndicator()
                    showToast(it.message.toString())
                }
            }
        }
    }

    private fun showLoadingIndicator() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        progressBar?.visibility = View.GONE
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
