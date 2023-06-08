/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("ReplaceNotNullAssertionWithElvisReturn")

package com.example.android.permissionrequest

import android.content.res.AssetManager
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

/**
 * Implementation of a very basic HTTP server. The contents are loaded from the assets folder. This
 * server handles one request at a time. It only supports GET method.
 */
class SimpleWebServer
/**
 * WebServer constructor. We save our parameters in their respective fields.
 *
 * @param port port we are to listen to.
 * @param mAssets `AssetManager` to use to access assets
 */(
    /**
     * The port number we listen to
     */
    val port: Int,
    /**
     * [android.content.res.AssetManager] for loading files to serve.
     */
    private val mAssets: AssetManager) : Runnable {
    /**
     * Getter for the `mPort` field, returns the port number we listen to.
     *
     * @return port number we listen to
     */

    /**
     * True if the server is running.
     */
    private var mIsRunning = false

    /**
     * The [java.net.ServerSocket] that we listen to.
     */
    private var mServerSocket: ServerSocket? = null

    /**
     * This method starts the web server listening to the specified port. We set our flag `mIsRunning`
     * to true, then create a new thread for us to run in and start that thread running.
     */
    fun start() {
        mIsRunning = true
        Thread(this).start()
    }

    /**
     * This method stops the web server. Wrapped in a try block intended to catch and log IOException
     * we set our flag `mIsRunning` to false, and if `mServerSocket` is not null we call
     * its `close` method to close the socket and set `mServerSocket` to null.
     */
    fun stop() {
        try {
            mIsRunning = false
            if (null != mServerSocket) {
                mServerSocket!!.close()
                mServerSocket = null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error closing the server socket.", e)
        }
    }

    /**
     * Method called when our thread's `start` method is called. Wrapped in a try block which
     * catches and ignores SocketException, and logs IOException we initialize our field
     * `ServerSocket mServerSocket` with a server socket, bound to the port `mPort`.
     * Then looping while our flag `mIsRunning` is true we initialize `Socket socket`
     * by listening for a connection to be made to the socket `mServerSocket` and accepting
     * it. We then call our method `handle` to respond to the request made by `socket`,
     * closing the socket when `handle` returns.
     */
    override fun run() {
        try {
            mServerSocket = ServerSocket(port)
            while (mIsRunning) {
                val socket = mServerSocket!!.accept()
                handle(socket)
                socket.close()
            }
        } catch (e: SocketException) {
            // The server was stopped; ignore.
        } catch (e: IOException) {
            Log.e(TAG, "Web server error.", e)
        }
    }

    /**
     * Respond to a request from a client. We initialize our variables `BufferedReader reader`
     * and `PrintStream output` to null. Then wrapped in a try block whose finally block closes
     * these two if they are not null, we initialize `String route` to null, then create a
     * new `BufferedReader` for `reader` to read from an `InputStreamReader` created
     * from the input stream for reading bytes from the socket `socket`. We declare `String line`
     * and loop while setting `line` to the next line of text read from `reader` is not
     * an empty string. If `line` starts with the string "GET /" we set `start` to 1 past
     * the '/', and `end` to the next ' ' after start and set `route` to the characters
     * between `start` and `end` then break out of the loop.
     *
     *
     * We set `output` to a new `PrintStream` from an output stream of `socket`.
     * If `route` is null we call our method `writeServerError` to write the error message
     * "HTTP/1.0 500 Internal Server Error" to `output` and return. Otherwise we initialize
     * `byte[] bytes` with the data that our method `loadContent` reads from the asset
     * file with the name `route`. If this is null we call our method `writeServerError`
     * to write the error message "HTTP/1.0 500 Internal Server Error" to `output` and return.
     *
     *
     * Having gotten this far without error we send our content to `output` line by line:
     *
     *  * "HTTP/1.0 200 OK"
     *  * "Content-Type: " with the mime type determined by our method `detectMimeType` for `route`
     *  * "Content-Length: " followed by the string value of the length of `bytes`
     *  * a blank line
     *  * The contents of the array `bytes`
     *
     * Finally we flush `output`.
     *
     * @param socket The client socket.
     * @throws IOException if an IO error occurs.
     */
    @Throws(IOException::class)
    private fun handle(socket: Socket) {
        var reader: BufferedReader? = null
        var output: PrintStream? = null
        try {
            var route: String? = null

            // Read HTTP headers and parse out the route.
            reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            var line: String
            while (!TextUtils.isEmpty(reader.readLine().also { line = it })) {
                if (line.startsWith("GET /")) {
                    val start = line.indexOf('/') + 1
                    val end = line.indexOf(' ', start)
                    route = line.substring(start, end)
                    break
                }
            }

            // Output stream that we send the response to
            output = PrintStream(socket.getOutputStream())

            // Prepare the content to send.
            if (null == route) {
                writeServerError(output)
                return
            }
            val bytes = loadContent(route)
            if (null == bytes) {
                writeServerError(output)
                return
            }

            // Send out the content.
            output.println("HTTP/1.0 200 OK")
            output.println("Content-Type: " + detectMimeType(route))
            output.println("Content-Length: " + bytes.size)
            output.println()
            output.write(bytes)
            output.flush()
        } finally {
            output?.close()
            reader?.close()
        }
    }

    /**
     * Writes a server error response (HTTP/1.0 500) to the given output stream, then flushes it.
     *
     * @param output The output stream.
     */
    private fun writeServerError(output: PrintStream) {
        output.println("HTTP/1.0 500 Internal Server Error")
        output.flush()
    }

    /**
     * Loads all the content of `fileName`. We initialize `InputStream input` to null.
     * Then wrapped in a try block intended to catch FileNotFoundException and return null and whose
     * finally block closes `input` if it is not null we:
     *
     *  * We initialize `ByteArrayOutputStream output` with a new instance
     *  * We use our field `AssetManager mAssets` to open the asset file `fileName` for `input`
     *  * We allocate 1024 bytes for `byte[] buffer`
     *  * We declare `int size`
     *
     * Then we loop reading from `input` into `buffer` saving the number of bytes read in
     * `size` so long as this is not -1 (end of file) and write `size` bytes from `buffer`
     * to `output` (with 0 as the offset). When done looping we flush `output` and return
     * a newly allocated byte array whose size is the current size of the output stream `output`
     * and whose contents is all the data in `output`
     *
     * @param fileName The name of the file.
     * @return The content of the file.
     * @throws IOException if an IO error occurs
     */
    @Throws(IOException::class)
    private fun loadContent(fileName: String): ByteArray? {
        var input: InputStream? = null
        return try {
            val output = ByteArrayOutputStream()
            input = mAssets.open(fileName)
            val buffer = ByteArray(1024)
            var size: Int
            while (-1 != input.read(buffer).also { size = it }) {
                output.write(buffer, 0, size)
            }
            output.flush()
            output.toByteArray()
        } catch (e: FileNotFoundException) {
            null
        } finally {
            input?.close()
        }
    }

    /**
     * Detects the MIME type from the `fileName`. If our parameter `fileName` is the
     * empty string we return null, otherwise we branch on the ending of the file name:
     *
     *  * ".html" we return the string "text/html"
     *  * ".js" we return the string "application/javascript"
     *  * ".css" we return the string "text/css"
     *  * any other ending we return the string "application/octet-stream"
     *
     *
     * @param fileName The name of the file.
     * @return A MIME type.
     */
    private fun detectMimeType(fileName: String): String? {
        return if (TextUtils.isEmpty(fileName)) {
            null
        } else if (fileName.endsWith(".html")) {
            "text/html"
        } else if (fileName.endsWith(".js")) {
            "application/javascript"
        } else if (fileName.endsWith(".css")) {
            "text/css"
        } else {
            "application/octet-stream"
        }
    }

    companion object {
        /**
         * TAG used for logging
         */
        private const val TAG = "SimpleWebServer"
    }
}