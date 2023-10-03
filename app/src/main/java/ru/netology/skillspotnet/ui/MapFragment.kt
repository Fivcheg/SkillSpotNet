package ru.netology.skillspotnet.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.skillspotnet.R
import ru.netology.skillspotnet.databinding.FragmentMapBinding

@AndroidEntryPoint
class MapFragment : Fragment() {

    companion object {
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"
        const val REQUEST_KEY = "PICK_POINT"
    }

    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private var addEvent = false
    private val bundle = Bundle()

    private val listener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            mapView?.map?.mapObjects?.clear()
        }

        override fun onMapLongTap(map: Map, point: Point) {
            mapView?.map?.mapObjects?.clear()
            val pin = requireNotNull(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.baseline_location_on_24
                )
            )
            val imageProvider = ImageProvider.fromBitmap(pin.toBitmap())
            val placemark = map.mapObjects.addPlacemark(point, imageProvider)
            placemark.addTapListener(placemarkTapListener)
            view?.findViewById<View>(R.id.button_set_point)?.setOnClickListener {
                bundle.apply {
                    putDouble("latitude", point.latitude)
                    putDouble("longitude", point.longitude)
                }
                setFragmentResult(REQUEST_KEY, bundle)
                findNavController().navigate(R.id.action_mapFragment_to_newEventFragment)
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    MapKitFactory.getInstance().resetLocationManagerToDefault()
                    userLocation.cameraPosition()?.target?.also {
                        val map = mapView?.map ?: return@registerForActivityResult
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
            map.addInputListener(listener)
            val arguments = arguments
            if (arguments != null &&
                arguments.containsKey(LAT_KEY) &&
                arguments.containsKey(LONG_KEY)
            ) {
                val point = Point(
                    arguments.getString(LAT_KEY)!!.toDouble(),
                    arguments.getString(LONG_KEY)!!.toDouble()
                )
                val cameraPosition = map.cameraPosition
                map.move(
                    CameraPosition(
                        point,
                        10F,
                        cameraPosition.azimuth,
                        cameraPosition.tilt,
                    )
                )
                val pin = requireNotNull(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.baseline_location_on_24
                    )
                )
                val imageProvider = ImageProvider.fromBitmap(pin.toBitmap())
                val placemark = map?.mapObjects?.addPlacemark(point, imageProvider)
                placemark?.addTapListener(placemarkTapListener)
                arguments.remove(LAT_KEY)
                arguments.remove(LONG_KEY)

                binding.buttonFindPoint.setOnClickListener {
                    map.move(
                        CameraPosition(
                            point,
                            10F,
                            cameraPosition.azimuth,
                            cameraPosition.tilt,
                        )
                    )
                }
            }
            if (arguments != null && arguments.containsKey("addEvent")) {
                addEvent = arguments.getBoolean("addEvent")
            } else {
                //TODO exception
                null
            }
            if (addEvent) {
                binding.buttonFindPoint.visibility = GONE
                binding.buttonSetPoint.visibility = VISIBLE
            } else {
                binding.buttonFindPoint.visibility = VISIBLE
                binding.buttonSetPoint.visibility = GONE
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
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private val placemarkTapListener = MapObjectTapListener { _, point ->
        Toast.makeText(
            context,
            "Super mega event (${point.longitude}, ${point.latitude})", Toast.LENGTH_SHORT
        ).show()
        true
    }
}
