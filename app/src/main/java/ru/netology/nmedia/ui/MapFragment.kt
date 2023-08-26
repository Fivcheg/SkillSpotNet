package ru.netology.nmedia.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentMapBinding

@AndroidEntryPoint
class MapFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
    }

    private lateinit var mapView: MapView
    private lateinit var userLocation: UserLocationLayer

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    MapKitFactory.getInstance().resetLocationManagerToDefault()
                    userLocation.cameraPosition()?.target?.also {
                        val map = mapView.map ?: return@registerForActivityResult
                        val cameraPosition = map.cameraPosition
                        map.move(
                            CameraPosition(
                                it,
                                cameraPosition.zoom,
                                cameraPosition.azimuth,
                                cameraPosition.tilt,
                            )
                        )
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.need_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.map.apply {
            userLocation = MapKitFactory.getInstance().createUserLocationLayer(mapWindow)
            userLocation.isVisible = true
            userLocation.isHeadingEnabled = false

            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        Point(arguments.getString(LAT_KEY)!!.toDouble(), arguments.getString(LONG_KEY)!!.toDouble()),
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt,
                    )
                )
                val imageProvider = ImageProvider.fromResource(context, R.drawable.baseline_location_on_24)
                val placemark = mapView.map.mapObjects.addPlacemark(Point(arguments.getString(LAT_KEY)!!.toDouble(), arguments.getString(LONG_KEY)!!.toDouble()), imageProvider)
                placemark.addTapListener(placemarkTapListener)

                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)
            } else {
                //TODO exception
                null
            }
        }

        binding.plus.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom + 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null
            )
        }

        binding.minus.setOnClickListener {
            binding.map.map.move(
                CameraPosition(
                    binding.map.map.cameraPosition.target,
                    binding.map.map.cameraPosition.zoom - 1, 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 0.3F),
                null,
            )
        }

        binding.location.setOnClickListener {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            context,
            "Tapped the point (${point.longitude}, ${point.latitude})", Toast.LENGTH_SHORT).show()
        true
    }
}
