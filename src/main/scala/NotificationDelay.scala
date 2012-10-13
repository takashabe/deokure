package com.takashabe.deokure

import android.app.{NotificationManager, PendingIntent, Notification}
import android.content.{Intent, Context}

case class NotificationDelay(context: Context) {

  def showNotification(delays: List[String]) {
    val builder = new Notification.Builder(context)
    builder.setTicker("Found Delay Railways")
    builder.setSmallIcon(android.R.drawable.ic_media_pause)
    builder.setWhen(0)

    builder.setContentTitle("Delay Railways:")
    builder.setContentText(delays.mkString(", "))

    val notifyIntent = new Intent(context, classOf[MainActivity])
    val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0)

    builder.setContentIntent(pendingIntent)

    val notification = builder.getNotification
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
    manager.notify(1, notification)
  }
}
