package com.takashabe.deokure

import android.os.Bundle
import android.content.{Loader, AsyncTaskLoader, Context, Intent}
import android.util.Log
import android.view.{MenuItem, MenuInflater, Menu, View}
import android.app.LoaderManager.LoaderCallbacks
import android.app.{ListFragment, FragmentTransaction}
import android.widget.{ListView, ImageView, TextView}
import android.graphics.drawable.Drawable
import android.net.Uri

class MainActivity extends TypedActivity {

  private lazy val fManager = getFragmentManager
  private var fragment = fManager.findFragmentByTag("fragment")

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

    val intent = new Intent(this, classOf[RouteGuideService])
    startService(intent)

    Option(fragment) match {
      case None => {
        fragment = new RouteListView
        val ft = fManager.beginTransaction()
        ft.add(android.R.id.content, fragment, "fragment")
        ft.commit()
      }
      case _ =>
    }

    // 路線リストの更新
    TwitterAnalyzer(getApplicationContext).writeAllRoutes()
  }

  override def onDestroy() {
    super.onDestroy()
    Log.d("deokure => ", "onDestory @ MainActivity")
  }

  def onClick(v: View) {
    Log.d("deokure => ", "deokure#onClick is down.")
    Log.d("deokure.log => ", v.getId.toString)
    val rf = fragment.asInstanceOf[RouteListView]
  }

  def onClickFavorite(v: View) {
  }

  class RouteListView extends ListFragment {
    private var mAdapter: CustomAdapter = null
    private var mData: List[RouteAdapterEntry] = null

    private val routeViewLoader = new LoaderCallbacks[List[(String, String)]] {
      override def onLoaderReset(loader: Loader[List[(String, String)]]) {}

      override def onLoadFinished(loader: Loader[List[(String, String)]], list: List[(String, String)]) {
        setAdapterList(list)
        setListShown(true)
      }

      override def onCreateLoader(id: Int, bundle: Bundle): Loader[List[(String, String)]] = {
        Log.d("deokure => ", "LoadTweetTask on CreateLoader.")

        new LoadTweetTask(MainActivity.this)
      }
    }

    def setAdapterList(data: List[(String, String)]) = {
      def isDelay(status: String): Drawable = {
        val delayRegex = """.*(delay).*""".r
        status match {
          case delayRegex("delay") => getResources.getDrawable(R.drawable.alerts_and_states_warning)
          case _ => {
            val rs = getResources.getDrawable(R.drawable.alerts_and_states_warning)
            rs.mutate().setAlpha(0)
            rs
          }
        }
      }

      def isFav(status: String): Drawable = {
        val favRegex = """.*(favorite).*""".r
        status match {
          case favRegex("favorite") => getResources.getDrawable(R.drawable.rating_important)
          case _ => getResources.getDrawable(R.drawable.rating_not_important)
        }
      }

      mData = data.map(x => RouteAdapterEntry(x._1, isFav(x._2), isDelay(x._2)))
      mAdapter.setData(mData)
    }

    def reloadRoutes() = {
      val all   = TwitterAnalyzer(getApplicationContext).readRouteList
      ViewMode.getMode match {
        case ViewMode.Delay => {
          val filterList = TwitterAnalyzer(getApplicationContext).readOnlyDelayStatus
          val filter = all.collect{ case x if filterList.contains(x._1) => x}
          Log.d("deokure.log => ", filter.toString())
          setAdapterList(filter)
        }
        case ViewMode.Favorite => {
          val filterList = TwitterAnalyzer(getApplicationContext).readOnlyFavRoute
          val filter = all.collect{ case x if filterList.contains(x._1) => x}
          Log.d("deokure.log => ", filter.toString())
          setAdapterList(filter)
        }
        case ViewMode.All => {
          setAdapterList(all)
        }
      }
    }

    override def onActivityCreated(savedInstanceState: Bundle) {
      super.onActivityCreated(savedInstanceState)
      setHasOptionsMenu(true)
      setListShown(false)

      mAdapter = new CustomAdapter(getActivity, 0)
      setListAdapter(mAdapter)

      getLoaderManager.initLoader(0, null, routeViewLoader)
    }

    /**
     * ListFragment内のアイコンがクリックされた時に呼ばれる。
     * ふぁぼのtoggleトリガーに使用する。
     * @param l
     * @param v
     * @param position
     * @param id
     */
    override def onListItemClick(l: ListView, v: View, position: Int, id: Long) {
      Option(mData) match {
        case None =>
        case Some(x) => TwitterAnalyzer(getApplicationContext).toggleFav(x(position).text)
      }

      reloadRoutes()
    }

    // create ActionBar item. items source is res/menu/menu.xml
    override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      inflater.inflate(R.menu.menu, menu)
      true
    }

    /**
     * define the action when clicked menu items.
     */
    override def onOptionsItemSelected(item: MenuItem): Boolean = {
      item.getItemId match {
        case R.id.refresh => {
          ViewMode.setMode(ViewMode.All)
          getLoaderManager.restartLoader(0, null, routeViewLoader)
          super.onOptionsItemSelected(item)
          true
        }
        case R.id.favorite => {
          ViewMode.setMode(ViewMode.Favorite)
          reloadRoutes()
          super.onOptionsItemSelected(item)
          true
        }
        case R.id.delay => {
          ViewMode.setMode(ViewMode.Delay)
          reloadRoutes()
          super.onOptionsItemSelected(item)
          true
        }
        case R.id.all => {
          ViewMode.setMode(ViewMode.All)
          reloadRoutes()
          super.onOptionsItemSelected(item)
          true
        }
        case _ => false
      }
    }
  }

}

/**
 * 非同期でTwitterAnalyzer経由で遅延情報を取得する。
 * AsyncTaskLoaderの実装クラス。
 */
class LoadTweetTask(context: Context) extends AsyncTaskLoader[List[(String, String)]](context: Context) {
  override def loadInBackground = {
    Log.d("deokure => ", "LoadTweetTask#loadInBackGround start.")
    val thread = TwitterAnalyzer(context)
    val tweet = thread.collectTweet
    thread.updateDelayStatus(tweet.unzip._1)
    thread.delayNotification(thread.readOnlyDelayStatus, thread.readOnlyFavRoute)
    thread.readRouteList
  }

  override def onStartLoading() {
    forceLoad()
  }
}

/**
 * 開いている画面の状態を保存する。
 * 遅延一覧画面のままふぁぼ切り替えたりするのに必要。
 */
object ViewMode {
  private var Mode = 0
  val All = 0         // 路線一覧画面
  val Delay = 1       // 遅延路線一覧画面
  val Favorite = 2    // ふぁぼ路線一覧画面

  def setMode(i: Int) = {
    i match {
      case All => Mode = All
      case Delay => Mode = Delay
      case Favorite => Mode = Favorite
      case _ => Mode = All
    }
    Log.d("deokure.log => setMode =", Mode.toString)
  }

  def getMode = Mode
}