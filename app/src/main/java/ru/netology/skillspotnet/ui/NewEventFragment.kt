package ru.netology.skillspotnet.ui

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.FragmentNewEventBinding
import ru.netology.skillspotnet.dto.Event
import ru.netology.skillspotnet.enumeration.AttachmentType
import ru.netology.skillspotnet.util.AndroidUtils
import ru.netology.skillspotnet.util.formatToDate
import ru.netology.skillspotnet.util.formatToInstant
import ru.netology.skillspotnet.util.pickDate
import ru.netology.skillspotnet.util.pickTime
import ru.netology.skillspotnet.viewmodel.EventViewModel


@AndroidEntryPoint
class NewEventFragment : Fragment() {

    private var fragmentBinding: FragmentNewEventBinding? = null
    private val viewModel: EventViewModel by activityViewModels()

    private var latitude: Double? = null
    private var longitude: Double? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentBinding = binding
        var type: AttachmentType? = null

        setFragmentResultListener(MapFragment.REQUEST_KEY) { _, bundle ->
            latitude = bundle.getDouble("lat")
            longitude = bundle.getDouble("long")
            binding.textEditInputLatitudeCoordsEvent.setText(latitude.toString())
            binding.textEditInputLongitudeCoordsEvent.setText(longitude.toString())
        }

        val datetime = arguments?.getString("datetime")?.let {
            formatToDate(it)
        } ?: formatToDate("${viewModel.edited.value?.datetime}")
        val date = datetime.substring(0, 10)
        val time = datetime.substring(11)

        binding.edit.setText(
            arguments?.getString("content") ?: viewModel.edited.value?.content
        )

        binding.textEditInputLatitudeCoordsEvent.setText(
            arguments?.getString("LAT_KEY") ?: viewModel.edited.value?.coords?.lat.toString()
        )

        binding.textEditInputLongitudeCoordsEvent.setText(
            arguments?.getString("LONG_KEY") ?: viewModel.edited.value?.coords?.long.toString()
        )

        if (viewModel.edited.value != Event.emptyEvent) {
            binding.textEditInputDateEvent.setText(date)
            binding.textEditInputEventTime.setText(time)
        }

        if (binding.textEditInputLatitudeCoordsEvent.text.toString() != "null" || binding.textEditInputLongitudeCoordsEvent.text.toString() != "null") {
            binding.textEditInputLatitudeCoordsEvent.visibility = VISIBLE
            binding.textEditInputLongitudeCoordsEvent.visibility = VISIBLE
        }

        binding.edit.requestFocus()

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changeMedia(uri, type)
                    }
                }
            }

        val pickMediaLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    viewModel.changeMedia(it, type)
                }
            }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
            type = AttachmentType.IMAGE
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
            type = AttachmentType.IMAGE
        }

        binding.removeMedia.setOnClickListener {
            viewModel.changeMedia(null, null)
        }

        binding.pickAudio.setOnClickListener {
            pickMediaLauncher.launch("audio/*")
            type = AttachmentType.AUDIO
        }

        binding.pickVideo.setOnClickListener {
            pickMediaLauncher.launch("video/*")
            type = AttachmentType.VIDEO
        }


        binding.pickLocation.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("addEvent", true)
            findNavController().navigate(R.id.mapFragment, bundle)
        }

        binding.pickSpeakers.setOnClickListener {
            val bundle = Bundle().apply {
                putString("PRESS_ADD", "PICK_SPEAKER")
            }
            findNavController().navigate(R.id.userFragment, bundle)
        }

        binding.pickParticipants.setOnClickListener {
            val bundle = Bundle().apply {
                putString("PRESS_ADD", "PICK_PARTICIPANTS")
            }
            findNavController().navigate(R.id.userFragment, bundle)
        }

        binding.textEditInputDateEvent.setOnClickListener {
            context?.let { item ->
                pickDate(binding.textEditInputDateEvent, item)
            }
        }

        binding.textEditInputEventTime.setOnClickListener {
            context?.let { item ->
                pickTime(binding.textEditInputEventTime, item)
            }
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            val receiveSpeaker = viewModel.edited.value?.speakerIds?.count().toString()
            val receiveParticipated = viewModel.edited.value?.participantsIds?.count().toString()
            binding.apply {
                pickSpeakers.apply {
                    text = receiveSpeaker
                }
                pickParticipants.apply {
                    text = receiveParticipated
                }
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.mediaContainer.visibility = GONE
                return@observe
            }
            binding.mediaContainer.visibility = VISIBLE
            binding.media.setImageURI(it.uri)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_new_post, menu)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentBinding?.let {
                            viewModel.changeContent(it.edit.text.toString())
                            viewModel.changeDateTime(
                                formatToInstant(
                                    "${it.textEditInputDateEvent.text} " +
                                            "${it.textEditInputEventTime.text}"
                                )
                            )
                            viewModel.changeCoords(
                                (latitude ?: viewModel.edited.value?.coords?.lat).toString(),
                                (longitude ?: viewModel.edited.value?.coords?.long).toString()
                            )
                            viewModel.saveEvent()
                            AndroidUtils.hideKeyboard(requireView())
                            findNavController().navigate(R.id.containerFragmentView)
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