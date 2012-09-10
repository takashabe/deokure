package com.takashabe.deokure

import android.os.AsyncTask

abstract class MyAsyncTask[A, B, C] extends AsyncTask[A, B, C] {
  override protected def doInBackground(values: A*): C = { doInBackgroundImpl(values: _*) }
  protected def doInBackgroundImpl(values: A*): C
  override protected def onProgressUpdate(progress: B*) = { onProgressUpdateImpl(progress: _*) }
  protected def onProgressUpdateImpl(progress: B*)
}
