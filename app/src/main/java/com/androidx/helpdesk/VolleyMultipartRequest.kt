package com.androidx.helpdesk

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.charset.Charset

open class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
) : Request<JSONObject>(method, url, errorListener) {

    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-${System.currentTimeMillis()}"

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    override fun getBody(): ByteArray? {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        return try {
            // populate text payload
            val params = params
            if (params != null && params.isNotEmpty()) {
                textParse(dos, params, paramsEncoding)
            }

            // populate data byte payload
            val data = getByteData()
            if (data != null && data.isNotEmpty()) {
                dataParse(dos, data)
            }

            // close multipart form data after text and file data
            dos.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")

            return bos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        return try {
            val jsonString = String(response.data,
                Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
            Response.success(JSONObject(jsonString),
                HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (je: JSONException) {
            Response.error(ParseError(je))
        }

    }

    override fun deliverResponse(response: JSONObject) {
        listener.onResponse(response)
    }

    @Throws(AuthFailureError::class)
    open fun getByteData(): Map<String, DataPart>? {
        return null
    }

    @Throws(IOException::class)
    private fun textParse(dataOutputStream: DataOutputStream, params: Map<String, String>, encoding: String) {
        try {
            for ((key, value) in params) {
                buildTextPart(dataOutputStream, key, value)
            }
        } catch (uee: UnsupportedEncodingException) {
            throw RuntimeException("Encoding not supported: $encoding", uee)
        }

    }


    @Throws(IOException::class)
    private fun dataParse(dataOutputStream: DataOutputStream, data: Map<String, DataPart>) {
        for ((key, value) in data) {
            buildDataPart(dataOutputStream, value, key)
        }
    }


    @Throws(IOException::class)
    private fun buildTextPart(dataOutputStream: DataOutputStream, parameterName: String, parameterValue: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        // dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8$lineEnd")
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(parameterValue + lineEnd)
    }


    @Throws(IOException::class)
    private fun buildDataPart(dataOutputStream: DataOutputStream, dataFile: DataPart, inputName: String) {
        dataOutputStream.writeBytes("$twoHyphens$boundary$lineEnd")
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$inputName\"; filename=\"${dataFile.fileName}\"$lineEnd")
        if (dataFile.type != null && !dataFile.type.isBlank()) {
            dataOutputStream.writeBytes("Content-Type: ${dataFile.type}$lineEnd")
        }
        dataOutputStream.writeBytes(lineEnd)

        val fileInputStream = ByteArrayInputStream(dataFile.content)
        var bytesAvailable = fileInputStream.available()

        val maxBufferSize = 1024 * 1024
        var bufferSize = Math.min(bytesAvailable, maxBufferSize)
        val buffer = ByteArray(bufferSize)

        var bytesRead = fileInputStream.read(buffer, 0, bufferSize)

        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize)
            bytesAvailable = fileInputStream.available()
            bufferSize = Math.min(bytesAvailable, maxBufferSize)
            bytesRead = fileInputStream.read(buffer, 0, bufferSize)
        }

        dataOutputStream.writeBytes(lineEnd)
    }


    data class DataPart(
        val fileName: String,
        val content: ByteArray,
        val type: String? = null
    )
}