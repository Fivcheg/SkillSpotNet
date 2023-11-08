package ru.netology.skillspotnet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.adapter.JobAdapter
import ru.netology.skillspotnet.auth.AppAuth
import ru.netology.skillspotnet.databinding.FragmentJobsBinding
import ru.netology.skillspotnet.dto.Job
import ru.netology.skillspotnet.repository.JobRepository
import ru.netology.skillspotnet.viewmodel.AuthViewModel
import ru.netology.skillspotnet.viewmodel.JobViewModel
import ru.netology.skillspotnet.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class JobFragment : Fragment() {
    @Inject
    lateinit var repository: JobRepository

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: JobViewModel by activityViewModels()
    private val userModel: UserViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentJobsBinding.inflate(inflater, container, false)

        val adapter = JobAdapter(object : JobAdapter.OnJobInteractionListener {
            override fun onEditJob(job: Job) {
                viewModel.edit(job)
                val bundle = Bundle().apply {
                    putString("name", job.name)
                    putString("position", job.position)
                    putString("start", job.start)
                    job.finish?.let {
                        putString("finish", it)
                    }
                    job.link?.let {
                        putString("link", it)
                    }
                }
                findNavController()
                    .navigate(R.id.newJobFragment, bundle)
            }

            override fun onRemoveJob(job: Job) {
                viewModel.removeById(job.id)
            }
        })

        val id = userModel.user.value?.id ?: authViewModel.data.value!!.id

        binding.listJobs.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitList)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.setId(id)
            viewModel.loadJobs(id)
        }

        binding.fabJob.visibility = if (id != auth.authStateFlow.value.id) {
            GONE
        } else {
            VISIBLE
        }

        binding.fabJob.setOnClickListener {
            findNavController().navigate(R.id.newJobFragment)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = JobFragment()
    }
}
