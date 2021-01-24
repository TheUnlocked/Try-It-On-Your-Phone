package com.github.theunlocked.tryitonyourphone.ui.code.runresult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.github.theunlocked.tryitonyourphone.MainActivity
import com.github.theunlocked.tryitonyourphone.R
import com.github.theunlocked.tryitonyourphone.model.RunRequest
import com.github.theunlocked.tryitonyourphone.network.Network
import com.github.theunlocked.tryitonyourphone.ui.code.CodeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.zip.Deflater
import java.util.zip.Deflater.BEST_COMPRESSION
import java.util.zip.Deflater.DEFAULT_COMPRESSION
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream


const val ENDPOINT = "https://tio.run/cgi-bin/run/api/"


class RunResultFragment : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_run_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val model = ViewModelProvider(activity as MainActivity).get(CodeViewModel::class.java)

        val query = RunRequest(
            model.language.value!!,
            model.code.value!!,
            "",
            model.cflags.value!!,
            model.options.value!!,
            model.argv.value!!
        ).toString().toByteArray()

        val compressionStream = ByteArrayOutputStream()
        DeflaterOutputStream(compressionStream, Deflater(DEFAULT_COMPRESSION, true)).apply {
            write(query)
            flush()
            close()
        }

        val request = Request.Builder()
            .url(ENDPOINT)
            .method("POST", compressionStream.toByteArray().toRequestBody())
            .build()

        Network.client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val rawOutput = response.body!!.string()

                val sep = rawOutput.substring(0, 16)
                val (output, debug) = rawOutput.substring(16).split(sep)

                activity?.runOnUiThread {
                    view.findViewById<LinearLayout>(R.id.result_output_group).visibility =
                        View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.loading_bar).visibility = View.GONE
                    view.findViewById<TextView>(R.id.result_output).text = output
                    view.findViewById<TextView>(R.id.result_debug).text = debug
                }
            }
        })
    }
}