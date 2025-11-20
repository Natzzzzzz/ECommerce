package com.example.ecommerce.features

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.example.ecommerce.R
import com.example.ecommerce.utils.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private var isBottomNavVisible = true
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollView)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        btnLogout = view.findViewById(R.id.btnLogout)

        btnLogout.setOnClickListener {
            TokenManager.clearToken(requireContext())
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show()

        }


        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && isBottomNavVisible) {
                bottomNav.animate().translationY(bottomNav.height.toFloat()).setDuration(200)
                isBottomNavVisible = false
            } else if (scrollY < oldScrollY && !isBottomNavVisible) {
                bottomNav.animate().translationY(0f).setDuration(200)
                isBottomNavVisible = true
            }
        })
    }
}