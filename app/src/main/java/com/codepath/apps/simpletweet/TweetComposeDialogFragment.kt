package com.codepath.apps.simpletweet

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.DialogFragment

class TweetComposeDialogFragment : DialogFragment() {
    // this is called when either the save or discard happens
   var callback : OnDialogAction? = null

    companion object {
        val TAG = "TweetComposeFragment"
        fun newInstance(title: String, onActionCallback : OnDialogAction?) : TweetComposeDialogFragment {
            val fragment = TweetComposeDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", title)

            fragment.callback = onActionCallback
            fragment.arguments = bundle

            return fragment
        }
    }

    interface OnDialogAction {
        abstract fun onSubmitCallback()
        abstract fun onCancelCallback()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString("title")
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Save Draft?")
            .setMessage(title)
            .setPositiveButton("Save", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    callback?.onSubmitCallback()
                }
            })
            .setNegativeButton("Discard", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    callback?.onCancelCallback()
                }
            })

        return alertDialogBuilder.create()
    }
}