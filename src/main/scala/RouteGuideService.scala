package com.takashabe.deokure

import android.content._
import android.app._
import android.widget.Toast

class RouteGuideService(s: String) extends IntentService(s: String) {
  def this() = this("")

  override def onDestroy() = super.onDestroy()

  override def onHandleIntent(intent: Intent) {
    new Thread() {
      override def run() {
        def pullTweetService(interval: Long) {
          val thread = TwitterAnalyzer(getApplicationContext)
          thread.collectTweet match {
            case Nil => Toast.makeText(getApplicationContext, "エラーが発生しました", Toast.LENGTH_LONG)
            case s: List[String] => {
              thread.updateDelayStatus(s.unzip._1)
              thread.delayNotification(thread.readOnlyDelayStatus, thread.readOnlyFavRoute)
              Thread.sleep(interval)
              pullTweetService(ResourceHandler.getPullTwitterInterval(getResources))
            }
          }
        }
        pullTweetService(ResourceHandler.getPullTwitterInterval(getResources))
      }
    }.start()
  }
}