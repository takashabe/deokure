package com.takashabe.deokure

import android.app.Activity
import android.os.Bundle
import android.content._
import android.app.LoaderManager._
import android.util.Log

class MainActivity extends TypedActivity with LoaderCallbacks[Unit] {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

    val text = findView(TR.textview)

    //SharedPreferencesの起動について(実際はサービス内でやる感じかなー)
    // val sp = getSharedPreferences("routePreference", Context.MODE_PRIVATE)
    // val ed = sp.edit()
    // ed.putString("hoge", "fuga")
    // ed.commit()

    // val value = sp.getString("fuga", "none")
    // Log.d("value => ", value)


    // val s = new RouteGuide(sp)
    // Log.d("value =>", s.findDelayState("hoge"))

    //サービスの起動について
    // val si = new Intent(this, classOf[Back])
    // startService(si)

    //twitter呼び出し
    /*
     * val twitter = TwitterHandler
     * twitter.searchDelay("takashabe")
     */

    onCreateLoader(0, bundle)
  }

  override def onCreateLoader(id: Int, args: Bundle): Loader[Unit] = {
    val task = new MyAsyncTaskLoader(this)
    task.forceLoad()
    return task
  }

  override def onLoadFinished(l: Loader[Unit], u: Unit): Unit = { Log.d("Log.d => ", "onLoadFinished()")}
  override def onLoaderReset(l: Loader[Unit]): Unit = { Log.d("Log.d => ", "onLoaderReset()")}
}

class MyAsyncTaskLoader(context: Context) extends AsyncTaskLoader[Unit](context) {
  override def loadInBackground(): Unit = {
    Log.d("Log.d => ", "loadInBackground()")
    val twitter = TwitterHandler
    val ss =twitter.searchDelay("takashabe")
    ss foreach { println}
  }
}
