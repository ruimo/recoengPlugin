package com.ruimo.recoeng

import java.util.concurrent.atomic.AtomicLong
import play.api._

object SequenceNumber {
  private val seed = new AtomicLong
  def apply: Long = seed.incrementAndGet
}

trait RecoEngApi {
  def onSales(
    requestTime: Long = System.currentTimeMillis,
    sequenceNumber: Long = SequenceNumber(),
    transactionMode: TransactionMode,
    transactionTime: Long,
    userCode: String,
    itemTable: Seq[SalesItem]
  )
}

class RecoEngApiImpl(plugin: RecoEngPlugin) extends RecoEngApi {
}

class RecoEngPlugin(val app: Application) extends Plugin {
  val logger = Logger(getClass)
  val config: Option[RecoEngConfig] = RecoEngConfig.get(app.configuration)
  val api: RecoEngApi = new RecoEngApiImpl(this)

  override def onStart() {
    logger.info("RecoEng Plugin started " + config)
  }

  override def onStop() {
    logger.info("RecoEng Plugin stopped...")
  }
}

object RecoEngPlugin {
  def api(implicit app: Application): RecoEngApi = app.plugin[RecoEngPlugin] match {
    case Some(plugin) => plugin.api
    case None => throw new Error("No recoeng plugin found in this application>")
  }
}
