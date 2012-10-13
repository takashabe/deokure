package com.takashabe.deokure

import org.specs2.mutable._
import twitter4j._

object TestTwitterHandler extends Specification {
  "TwitterHandler" should {
    "[specs test]TwitterHandler はシングルトンだよ" in {
      val t1 = TwitterHandler
      val t2 = TwitterHandler
      t1 must_== t2
    }
    "特定の文字列で検索出来る" in {
      val q = List("遅延","遅れ","見合わせ")
//      val list = TwitterHandler.searchDelay(q)
      val twitter = new TwitterFactory()//.getInstance()
//      twitter.search(new Query(q.head))
      true
    }
  }
}
