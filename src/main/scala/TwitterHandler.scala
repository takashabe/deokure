package com.takashabe.deokure

import twitter4j._
import scala.collection.JavaConversions._

object TwitterHandler {
  lazy val twitter = new TwitterFactory().getInstance()

  def searchDelay(q: String): List[String] = {
    val res = twitter.search(new Query(q))
    val tweets = res.getTweets.toList
    val list: List[String] = tweets.map(_.getText)
    list.reverse
  }
}
