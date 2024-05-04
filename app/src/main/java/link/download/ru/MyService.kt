package link.download.ru

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class MyService : Service() {
    var id = ""

    lateinit var notificationManager: NotificationManager
    lateinit var builder: Notification.Builder
    var phone = ""
    private lateinit var dbRef: DatabaseReference
    private val channelId = "i.apps.notifications"


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("202012","service is started")
        buildNotification(this@MyService)

        return START_STICKY
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    fun buildNotification(context: Context?){

//        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        createNotificationChannel(channelId, "Сообщения одиночных чатов", "nMC")
//        val sharedPref = context.getSharedPreferences("login", Context.MODE_PRIVATE)
//        phone = sharedPref.getString("phone", "-").toString()
//        dbRef = FirebaseDatabase.getInstance().getReference("UserNotifications")
//        Log.d("202012", "MessageReceiver запущен")
//
//        dbRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//
//            }
//
//            @SuppressLint("ForegroundServiceType")
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                val data = snapshot.getValue(UserData::class.java)
//                val name = data!!.notname
//                val title = data.nottitle
//                val phone2 = data.phone
//                data.cophone
//                Log.d("202012","$name")
//
//                if (phone == phone2){
//                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
//                        builder = Notification.Builder(context, channelId)
//                            .setContentTitle(name)
//                            .setContentText(title)
//                            .setSmallIcon(R.drawable.logo_round)
//                            .setPriority(Notification.PRIORITY_HIGH)
//                        startForeground(1, builder.build())
//                    }
//                    else{
//                        builder = Notification.Builder(context, channelId)
//                            .setContentTitle(name)
//                            .setContentText(title)
//                            .setSmallIcon(R.drawable.logo_round)
//                            .setPriority(Notification.PRIORITY_HIGH)
//                        startForeground(1, builder.build())
//                    }
//                }
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//
//            }
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//        builder = Notification.Builder(context, channelId)
//            .setSmallIcon(R.drawable.invisible_pic)
//        startForeground(1, builder.build())
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        channelDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        Log.e("202012","service is destroyed")
    }
}