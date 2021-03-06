package lila
package monitor

import akka.actor._
import play.api.libs.concurrent._
import play.api.Application
import com.mongodb.casbah.MongoDB

import core.Settings

final class MonitorEnv(
  app: Application,
  mongodb: MongoDB,
  settings: Settings) {

  implicit val ctx = app
  import settings._

  lazy val hub = Akka.system.actorOf(
    Props(new Hub(timeout = SiteUidTimeout)), name = ActorMonitorHub)

  lazy val socket = new Socket(hub = hub)

  lazy val reporting = Akka.system.actorOf(
    Props(new Reporting(
      rpsProvider = rpsProvider,
      mongodb = mongodb,
      hub = hub
    )), name = ActorReporting)

  val rpsProvider = new RpsProvider(
    timeout = MonitorTimeout)
}
