package com.takashabe.deokure

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.content._
import android.app._

import android.os.IBinder
import android.widget.Toast

class RouteGuide(sp: SharedPreferences) extends TypedActivity {

  private val ed = sp.edit()

  def setDelayState(key: String, value : String) = {
    ed.putString(key, value)
    ed.commit()
  }

  def setUpdateTime(key: String, value: Long) = {
    ed.putLong(key, value)
    ed.commit()
  }

  def findDelayState(key: String): String = {
    sp.getString(key, "none")
  }

  def findUpdateTime(key: String): Long = {
    sp.getLong(key, -1L)
  }
}


class Back extends Service {
  var cnt = 0

  override def onBind(intent: Intent): IBinder = {
    null
  }

  override def onStartCommand(intent: Intent, flags: Int, id: Int): Int = {
    val sp = getSharedPreferences("routePreference", Context.MODE_PRIVATE)
    val rg = new RouteGuide(sp)

    def sub {
      cnt match {
        case _ if cnt < 5 => {
          Toast.makeText(Back.this, "サービス開始だよっ => " + write(cnt, rg), Toast.LENGTH_LONG).show
          cnt+=1
          Thread.sleep(1000)
          sub
        }
        case _ => Log.d("Log.d => サービスは終わったはずだよ", cnt.toString)
      }
    }
    sub
    stopSelf
    return Service.START_STICKY
  }

  def write(i: Int, rg: RouteGuide): String = {
    rg.setDelayState("count", i.toString + "番")
    rg.findDelayState("count")
  }

  override def onDestroy() = {
    Toast.makeText(this, "サービス終わり＞＜", Toast.LENGTH_LONG).show
  }
}
