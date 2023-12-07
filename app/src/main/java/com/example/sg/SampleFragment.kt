package com.example.sg

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.sg.common.Utils.Companion.uriToFile
import com.example.sg.databinding.FragmentSampleBinding
import com.example.sg.view.ImageUploadView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SampleFragment : Fragment(), ImageUploadView.ImageUploadListener {

    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SampleViewModel by activityViewModels()

    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)
        binding.imageUploadView.initialiseRegisterForResult(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        binding.imageUploadView.setImageUploadListener(this)
        liveDataObserver()
    }

    private fun liveDataObserver() {
        viewModel.responseImageUploadTest.observe(viewLifecycleOwner) {
            showLoadingIndicator()
            lifecycleScope.launch {
                // Simulate a delay of 5 seconds
                delay(5000)
                hideLoadingIndicator()
                Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLoadingIndicator() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        progressBar?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onImageSubmitted(imageUri: Uri?) {
        val file = uriToFile(requireContext(), imageUri!!)

        if (file != null) {
            lifecycleScope.launch {
                viewModel.uploadImage(file)
            }
        }
    }
}
