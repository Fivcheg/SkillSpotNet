package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.JobAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentJobsBinding
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.repository.JobRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.JobViewModel
import ru.netology.nmedia.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class JobFragment : Fragment() {
    @Inject
    lateinit var repository: JobRepository

    @Inject
    lateinit var auth: AppAuth
    private val viewModel: JobViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val userModel: UserViewModel by activityViewModels()
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


        //   val id = auth.authStateFlow.value.id
        val id = userModel.user.value!!.id


        binding.listJobs.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest(adapter::submitList)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.setId(id)
            viewModel.loadJobs(id)
        }

      //  if (id == auth.authStateFlow.value.id) {
            binding.fabJob.setOnClickListener {
                findNavController().navigate(R.id.action_containerFragmentView_to_newJobFragment)
            }
    //    }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = JobFragment()
    }
}
