package com.takashabe.deokure

import android.widget._
import android.content.Context
import android.view.{ViewGroup, View, LayoutInflater}
import android.util.Log
import android.graphics.drawable.Drawable
import scala.collection.JavaConversions._

class CustomAdapter(context: Context, resourceID: Int) extends ArrayAdapter[RouteAdapterEntry](context: Context, resourceID: Int) {
  private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE).asInstanceOf[LayoutInflater]

  def setData(data: List[RouteAdapterEntry]) {
    clear()
    Option(data) match {
      case None => Log.d(this.getClass.getName, "setData is null")
      case Some(d) => {
        Log.d(this.getClass.getName, "setData addAll()")
        val js: java.util.List[RouteAdapterEntry] = d
        addAll(js)
      }
    }
  }

  override def getView(position: Int, srcView: View, parent: ViewGroup): View = {
    val convertView: View = Option(srcView) match {
      case None    => mInflater.inflate(R.layout.main, parent, false)
      case Some(v) => v
    }

    val item = getItem(position)
    convertView.findViewById(R.id.icon).asInstanceOf[ImageView].setImageDrawable(item.fav)
    convertView.findViewById(R.id.delay).asInstanceOf[ImageView].setImageDrawable(item.delay)
    convertView.findViewById(R.id.text).asInstanceOf[TextView].setText(item.text)

    convertView
  }

}

case class RouteAdapterEntry(text: String, fav: Drawable, delay: Drawable)
