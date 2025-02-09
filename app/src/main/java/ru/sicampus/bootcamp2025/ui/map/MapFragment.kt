package ru.sicampus.bootcamp2025.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.BottomSheetDialogBinding
import ru.sicampus.bootcamp2025.domain.map.DepartmentEntity
import ru.sicampus.bootcamp2025.domain.map.PlaceEntity
import ru.sicampus.bootcamp2025.ui.list.ListFragment
import ru.sicampus.bootcamp2025.util.navigateTo


class MapFragment() : Fragment(R.layout.fragment_map),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by viewModels() { MapViewModel.Factory}
    private var isDepartmentNotNull = true
    private var detailsDialog: BottomSheetDialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        if (mapFragment == null) {
            Log.e("MapFragment", "MapFragment is null!")
        } else {
            mapFragment.getMapAsync(this)
        }

        viewModel.placesLiveData.observe(viewLifecycleOwner) { departments ->

            if (::googleMap.isInitialized) {
                for (department in departments) {
                    googleMap.addMarker(MarkerOptions().position(department.place.latLng).title(department.name))
                }
            }
        }
        viewModel.selectedDepartment.observe(viewLifecycleOwner) { department ->
            if (department != null) {
                showPlaceDetails(department, requireContext())
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMapLongClickListener(this)
        googleMap.setOnMarkerClickListener(this)

        val startPoint = LatLng(55.7558, 37.6176)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 10f))
        viewModel.getPlaces()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.forgetDepartment()
        detailsDialog?.dismiss()


    }
    override fun onMapClick(latLng: LatLng) {
        Toast.makeText(requireContext(), "Coords: ${latLng.latitude} ${latLng.longitude}", Toast.LENGTH_SHORT).show()
        viewModel.forgetDepartment()
    }

    override fun onMapLongClick(latLng: LatLng) {
        Toast.makeText(requireContext(), "LONG Coords: ${latLng.latitude} ${latLng.longitude}", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.getDepartmentByName(marker.title)
        return false
    }

    private fun showPlaceDetails(department: DepartmentEntity, context: Context) {
        detailsDialog?.dismiss()

        val binding = BottomSheetDialogBinding.inflate(LayoutInflater.from(context))
        val place = department.place
        detailsDialog = BottomSheetDialog(context).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Настройка содержимого диалога
            binding.image?.let {
                Glide.with(this@MapFragment)
                    .load(place.pathToImage)
                    .placeholder(R.drawable.ic_photo)
                    .error(R.drawable.ic_back)
                    .into(it)
            }
            binding.name.text = place.name
            binding.address.text = place.address
            binding.description.text = place.information

            binding.attach.setOnClickListener {
                binding.attach.text = "Вы прикреплены"
                viewModel.changeDepartmentAttach(department.name)
            }

            binding.checkPeople.setOnClickListener {
                viewModel.forgetDepartment()
                view?.let { it1 -> navigateTo(it1, R.id.action_nav_map_to_nav_user_list, Bundle().apply {
                    putString("filter_type", "department")
                    putString("departmentName", department.name)
                }) }
            }

            // Показываем диалог
            show()
        }
    }

}
