package com.example.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsapp.R
import com.moengage.inapp.MoEInAppHelper

class FifthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fifth, container, false)
    }

    override fun onStart() {
        super.onStart()
        MoEInAppHelper.getInstance().setInAppContext(setOf("fifth"))
        MoEInAppHelper.getInstance().showInApp(requireContext())
    }

    override fun onStop() {
        super.onStop()
        MoEInAppHelper.getInstance().resetInAppContext()
    }
}