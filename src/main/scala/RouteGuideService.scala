package com.takashabe.deokure

import android.content._
import android.app._

class RouteGuideService(s: String) extends IntentService(s: String) {
  def this() = { this("") }

  override def onDestroy() {
    super.onDestroy()
  }

  override def onHandleIntent(intent: Intent) {
    new Thread() {
      override def run() {
        def pullTweetService() {
          val thread = TwitterAnalyzer(getApplicationContext)
          val tweet = thread.collectTweet
          thread.updateDelayStatus(tweet.unzip._1)
          thread.delayNotification(thread.readOnlyFavRoute, thread.readOnlyFavRoute)
          Thread.sleep(600000)
          pullTweetService()
        }
        pullTweetService()
      }
    }.start()
  }
}