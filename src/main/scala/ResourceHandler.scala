package com.takashabe.deokure

import android.content.res.Resources

object ResourceHandler {
  def getQueryParams(res: Resources): List[String] = {
    res.getStringArray(R.array.query).toList
  }

  //    鉄道会社ごとの路線をまとめて返す
  //    e.g. JR ::: routes
  def getRouteParams(res: Resources): List[String] = {
    val JR = res.getStringArray(R.array.JR).toList
    val routes = Nil
    JR ::: routes
  }

  def getPullTwitterInterval(res: Resources): Long = res.getInteger(R.integer.interval)
}
