package com.takashabe.deokure

import twitter4j._
import scala.collection.JavaConversions._

object TwitterHandler {
  val twitter = new TwitterFactory().getInstance()

  def searchDelay(q: List[String]): Either[TwitterException, List[QueryResult]] = {
    try {
      Right(q.map(x => twitter.search(new Query(x))).toList)
    } catch {
      case e: TwitterException => Left(e)
    }
  }

}

object TweetCollector {
  def findRouteInfo(query: List[String]): List[(String, Long)] = {
    val res: List[Tweet] = searchTweet(query)
    val twText = res.map(_.getText)
    val twTime = res.map(_.getCreatedAt.getTime)
    twText zip twTime
  }

  def searchTweet(query: List[String]) = {
    val result: List[Tweet] = TwitterHandler.searchDelay(query) match {
      case Left(e) => Nil
      case Right(xs) => xs.toList.map(_.getTweets.toList).flatten
    }
    //TODO: RTを除外したりして精度を上げる
    //TODO: 取得Tweet数を調整する(デフォ20だけだから200くらいかな？もしくは時間指定とかで）

    result
  }
}
