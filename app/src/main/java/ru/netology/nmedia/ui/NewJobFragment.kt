package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewJobBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.selectDateDialog
import ru.netology.nmedia.util.pickDate
import ru.netology.nmedia.viewmodel.JobViewModel


@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val viewModel: JobViewModel by activityViewModels()

    private var fragmentBinding: FragmentNewJobBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentBinding = binding

        with(binding) {
            textEditInputCompanyJob.setText(arguments?.getString("name"))
            textEditInputPositionJob.setText(arguments?.getString("position"))
            textEditInputStartJob.setText(arguments?.getString("start"))
            textEditInputFinishJob.setText(arguments?.getString("finish"))
            textEditInputLinkJob.setText(arguments?.getString("link"))
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.textEditInputStartJob.setOnClickListener {
            selectDateDialog(binding.textEditInputStartJob, requireContext())
            val startDate = binding.textEditInputStartJob.text.toString()
            viewModel.startDate(startDate)
        }

        binding.textEditInputFinishJob.setOnClickListener {
            selectDateDialog(binding.textEditInputFinishJob, requireContext())
            val endDate = binding.textEditInputFinishJob.text.toString()
            viewModel.finishDate(endDate)
        }

        binding.textEditInputStartJob.setOnClickListener {
            context?.let {
                pickDate(binding.textEditInputStartJob, it)
            }
        }
        binding.textEditInputFinishJob.setOnClickListener {
            context?.let {
                pickDate(binding.textEditInputFinishJob, it)
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentBinding?.let {
                            if (
                                (it.textEditInputCompanyJob.text != null) &&
                                (it.textEditInputPositionJob.text != null) &&
                                (it.textEditInputStartJob.text != null)
                            ) {
                                viewModel.editJob(
                                    name = it.textEditInputCompanyJob.text.toString(),
                                    position = it.textEditInputPositionJob.text.toString(),
                                    start = it.textEditInputStartJob.text.toString(),
                                    finish = it.textEditInputFinishJob.text.toString(),
                                    link = it.textEditInputLinkJob.text.toString()
                                )
                                viewModel.save()
                                AndroidUtils.hideKeyboard(requireView())
                            }
                        }
                        true
                    }

                    else -> false
                }

        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}