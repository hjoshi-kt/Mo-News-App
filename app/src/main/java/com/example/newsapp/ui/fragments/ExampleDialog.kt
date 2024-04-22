package com.example.newsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu.OnDismissListener
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.util.InApp
import com.example.newsapp.util.Utils

class ExampleDialog(private val fragmentManager: FragmentManager, val inApp: InApp, private val context: Context, private val listener: ClickListener) : DialogFragment() {

    init {
        this.show(fragmentManager, TAG)
    }

    private lateinit var toolbar: Toolbar
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var imageView: ImageView
    private lateinit var messageLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.example_dialog, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        title = view.findViewById(R.id.title)
        message = view.findViewById(R.id.message)
        imageView = view.findViewById(R.id.myImage)
        messageLayout = view.findViewById(R.id.messageLayout)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            listener.OnDismissListener(context)
            dismiss()
        }
        toolbar.title = "In App"
        title.text = inApp.title
        message.text = inApp.message
        Glide.with(this)
            .load(inApp.imageLink)
            .placeholder(R.drawable.baseline_downloading_24)
            .into(imageView)
        messageLayout.setOnClickListener{
            listener.OnClickListener(context)
        }
    }

    companion object {
        const val TAG = "example_dialog"
    }
}

interface ClickListener {
    fun OnDismissListener(context: Context)
    fun OnClickListener(context: Context)
}
