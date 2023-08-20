package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageBinding

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ImageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding = FragmentImageBinding.inflate(inflater, container, false)

     //   (activity as AppCompatActivity).supportActionBar?.hide()

        binding.apply {
            imageOpen.visibility = View.GONE
            val url = arguments?.getString("url")

            Glide.with(imageOpen)
                .load(url)
                .placeholder(R.drawable.baseline_catching_pokemon_24)
                .error(R.drawable.baseline_report_gmailerrorred_24)
                .timeout(10_000)
                .into(imageOpen)

            imageOpen.visibility = View.VISIBLE
        }

        binding.imageOpen.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}