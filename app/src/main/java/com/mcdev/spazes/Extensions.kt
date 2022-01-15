package com.mcdev.spazes

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.mcdev.spazes.util.BEARER_TOKEN
import kotlinx.coroutines.*
import okhttp3.*
import java.lang.Exception
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


private fun makeGetRequest(url: String): Request {
    return Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .header(
            "Authorization",
            "Bearer $BEARER_TOKEN"
        )
        .method("GET", null)
        .build()
}

fun getOkHttpClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}

//suspend fun Context.searchSpaces(query: String, state: SpacesState = SpacesState.ALL): SpacesResponse? {
//    var response: SpacesResponse? = null
//
//    withContext(Dispatchers.IO){
//        val url =
//            "https://api.twitter.com/2/spaces/search?" +
//                    "query=$query" +
//                    "&state=${state.value}" +
//                    "&space.fields=host_ids,created_at,creator_id,id,lang,invited_user_ids,participant_count,speaker_ids,started_at,state,title,updated_at,scheduled_start,is_ticketed" +
//                    "&expansions=invited_user_ids,speaker_ids,creator_id,host_ids" +
//                    "&user.fields=${UserField.NAME.value},${UserField.PROFILE_IMAGE_URL.value}"
//
//        val client: OkHttpClient = getOkHttpClient()
//        val request = makeGetRequest(url)
//
//        try {
//            response = client.newCall(request).await()
//
//            Log.d("TAG", "searchSpaces: code : ${response?.code}, message: ${response?.message}")
//        } catch (e: Exception) {
//            Log.d("TAG", "get spaces by search query: An exception occurred. $e")
//        }
//    }
//    return response
//}

internal suspend inline fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        val callback = ContinuationCallback(this, continuation)
        enqueue(callback)
        continuation.invokeOnCancellation(callback)
    }
}

internal class ContinuationCallback(
    private val call: Call,
    private val continuation: CancellableContinuation<Response>
) : Callback, CompletionHandler {

    override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
    }

    override fun onFailure(call: Call, e: IOException) {
        if (!call.isCanceled()) {
            continuation.resumeWithException(e)
        }
    }

    override fun invoke(cause: Throwable?) {
        try {
            call.cancel()
        } catch (t: Throwable) {}
    }
}

fun String.formatDateAndTime(): String {
    val INCOMING_DT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
    val DISPLAY_DT_PATTERN = "dd MMM yy \nhh:mm a"
    val incomingFormat = SimpleDateFormat(INCOMING_DT_PATTERN)
    val incDate = incomingFormat.parse(this)
    val displayFormat: SimpleDateFormat = SimpleDateFormat(DISPLAY_DT_PATTERN)

    val date = displayFormat.format(incDate!!)
    Log.d("TAG", "formatDateAndTime: $date")
    return date
}

fun Activity.makeStatusBarTransparent() {
    window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        statusBarColor = Color.TRANSPARENT
    }
}

fun Activity.changeStatusBarColor(color: Int) {
    val window: Window = this.window
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.statusBarColor = resources.getColor(color, this.theme)
    window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

}