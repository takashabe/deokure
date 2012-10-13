package com.takashabe.deokure

import android.content.{SharedPreferences, Context}

case class TwitterAnalyzer(context: Context) {
  // target SharedPreferences name.
  private lazy val AllSP = context.getSharedPreferences("allRoutes", Context.MODE_PRIVATE)
  private lazy val DelaySP = context.getSharedPreferences("delayRoutes", Context.MODE_PRIVATE)
  private lazy val FavoriteSP = context.getSharedPreferences("FavoriteRoutes", Context.MODE_PRIVATE)

  /**
   * Twitterから遅延情報を検索する。
   * 検索クエリはResourceのものを利用する。
   * @return
   *         String: 路線名
   *         Long: ツイートのunix時間
   */
  def collectTweet: List[(String, Long)] = {
    TweetCollector.findRouteInfo(ResourceHandler.getQueryParams(context.getResources))
  }

  def readRouteList: List[(String, String)] = {

    def addDelayStatus(target: (String, String), delayList: List[String]) = target match {
      case x if delayList.contains(x._1) => (x._1, x._2 + ",delay")
      case x => x
    }

    def addFavStatus(target: (String, String), favList: List[String]) = target match {
      case x if favList.contains(x._1) => (x._1, x._2 + ",favorite")
      case x => x
    }

    val allList: List[(String, String)] = (ResourceHandler getRouteParams context.getResources).map((_, ""))
    val delayList = readOnlyDelayStatus
    val favList = readOnlyFavRoute

    // delay Statusの付与
    val ad  = allList map(s => addDelayStatus(s, delayList))
    // favorite Statusの付与
    val adf = ad map(s => addFavStatus(s, favList))
    adf
  }

  /**
   * ふぁぼっている路線が遅延している場合にNotificationを実行する。
   * @param delayList 遅延路線名のリスト
   * @param favList   ふぁぼ路線名のリスト
   */
  def delayNotification(delayList: List[String], favList: List[String]) ={
    delayList.filter(favList.contains(_)) match {
      case Nil =>
      case x   => NotificationDelay(context).showNotification(x)
    }
  }

  /**
   * ふぁぼっている路線のみを返す。
   * @return ふぁぼっている路線名のリスト
   */
  def readOnlyFavRoute: List[String] = {
    val allRoutes = ResourceHandler getRouteParams context.getResources
    val favFilter = allRoutes map(StatusManager(FavoriteSP) findDelayStatus _)
    favFilter collect{ case x if x != "none" => x }
  }

  /**
   * 遅延している路線のみを返す。
   * @return 遅延している路線名のリスト
   */
  def readOnlyDelayStatus: List[String] = {
    val allRoutes = ResourceHandler getRouteParams context.getResources
    val delayFilter = allRoutes map(StatusManager(DelaySP) findDelayStatus _)
    delayFilter collect{ case x if x != "none" => x }
  }

  /**
   * 遅延情報がある路線の重複を排除してSharedPreferencesに保存する
   * @param findDelayList 遅延している路線名のリスト
   */
  def updateDelayStatus(findDelayList: List[String]) = {
    def filterDelay(key: String) = findDelayList.exists(_.contains(key)) match {
      case false => None
      case true  => Some(key)
    }

    val allRoutes   = ResourceHandler getRouteParams context.getResources
    val delayRoutes = allRoutes map(filterDelay _)
    writeDelayStatus(delayRoutes.collect {case Some(s) => s})
  }

  /**
   * ふぁぼ路線をtoggleで更新する
   * 既にふぁぼられている路線はふぁぼリストから除外する。
   * @param queryFav ふぁぼリクエストを行なっている路線名
   * @return 路線をふぁぼしたかどうかを返す。
   */
  def toggleFav(queryFav: String) = {

    val alreadyFav = StatusManager(FavoriteSP) findDelayStatus queryFav
    update(queryFav)

    def update(s: String): Boolean = s match {
      case _ if alreadyFav.contains(s) => {
        StatusManager(FavoriteSP).deleteStatus(List(s))
      }
      case _ => {
        StatusManager(FavoriteSP).setStatusList(List((s, s)))
      }
    }
  }

  // 全ての鉄道リストを更新する
  def writeAllRoutes() {
    val allRoutes = ResourceHandler getRouteParams context.getResources
    StatusManager(AllSP) setStatusList allRoutes.map((_, ""))
  }

  /**
   * 遅延している路線を保存する
   * @param delayRoutes 遅延路線名のリスト
   */
  def writeDelayStatus(delayRoutes: List[String]) {
    StatusManager(DelaySP) deleteStatus(ResourceHandler getRouteParams context.getResources)
    StatusManager(DelaySP) setStatusList delayRoutes.map(s => (s, s))
  }

  /**
   * 任意のSharedPreferencesの内容を削除する
   * @param sp 削除対象のSharedPreferences
   */
  def deleteSharedPreferences(sp: SharedPreferences) {
    StatusManager(sp).deleteStatus(ResourceHandler.getRouteParams(context.getResources))
  }
}
