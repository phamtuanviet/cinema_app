package com.example.myapplication.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.core.datastore.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sessionManager: SessionManager

    private val TAG = "MyFirebaseMsgService"

    // Hàm này chạy MỖI KHI CÓ THÔNG BÁO MỚI GỬI ĐẾN TỪ SERVER
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Kiểm tra xem tin nhắn có chứa dữ liệu ngầm (Data payload) không
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            // Bạn có thể lấy bookingId ra ở đây: val bookingId = remoteMessage.data["bookingId"]
        }

        // Kiểm tra xem tin nhắn có chứa khối Notification không (Tiêu đề, nội dung)
        remoteMessage.notification?.let {
            sendNotification(it.title ?: "Thông báo", it.body ?: "", remoteMessage.data)
        }
    }

    // Hàm này CHỈ CHẠY 1 LẦN khi app mới cài đặt hoặc khi token bị reset
    // Token này chính là cái bạn cần gửi lên Spring Boot để lưu vào database
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token (Background): $token")

        // Vì saveFcmToken là hàm suspend, phải bọc nó trong CoroutineScope
        CoroutineScope(Dispatchers.IO).launch {
            sessionManager.saveFcmToken(token)
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        // Viết code Retrofit / Ktor gọi API ở đây
        Log.d(TAG, "Sending token to server: $token")
    }

//    private fun sendNotification(title: String, messageBody: String) {
//        // Đổi MainActivity thành màn hình bạn muốn mở khi click vào thông báo
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val channelId = "movie_ticket_channel" // ID của kênh thông báo (bắt buộc từ Android 8+)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info) // Đổi thành icon app của bạn (VD: R.drawable.ic_movie)
//            .setContentTitle(title)
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Kể từ Android 8.0 (Oreo), bạn BẮT BUỘC phải tạo Notification Channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Thông báo đặt vé phim",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Hiện thông báo (dùng random ID để các thông báo không đè lên nhau)
//        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
//    }

    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Lấy dữ liệu ngầm từ Spring Boot gửi qua và nhét vào Intent
            data["action"]?.let { putExtra("action", it) }
            data["bookingId"]?.let { putExtra("bookingId", it) }
        }

        // Bắt buộc dùng FLAG_UPDATE_CURRENT để Intent mới đè lên Intent cũ
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "movie_ticket_channel" // ID của kênh thông báo (bắt buộc từ Android 8+)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Đổi thành icon app của bạn (VD: R.drawable.ic_movie)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Kể từ Android 8.0 (Oreo), bạn BẮT BUỘC phải tạo Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Thông báo đặt vé phim",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Hiện thông báo (dùng random ID để các thông báo không đè lên nhau)
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }
}