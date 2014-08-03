package com.ruimo.recoeng

import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.DateTime
import java.util.concurrent.atomic.AtomicLong
import com.ruimo.recoeng.json.OnSalesJsonRequest
import com.ruimo.recoeng.json.OnSalesJsonResponse
import com.ruimo.recoeng.json.JsonRequestHeader
import com.ruimo.recoeng.json.TransactionMode
import com.ruimo.recoeng.json.TransactionSalesMode
import com.ruimo.recoeng.json.SalesItem
import com.ruimo.recoeng.json.JsonResponseHeader
import play.api._

object SequenceNumber {
  private val seed = new AtomicLong
  def apply(): Long = seed.incrementAndGet
}

trait RecoEngApi {
  def onSales(
    requestTime: Long = System.currentTimeMillis,
    sequenceNumber: Long = SequenceNumber(),
    transactionMode: TransactionMode,
    transactionTime: Long,
    userCode: String,
    itemTable: Seq[SalesItem]
  ): JsResult[OnSalesJsonResponse]
}

class RecoEngApiImpl(
  plugin: RecoEngPlugin,
  serverFactory: RecoEngPlugin => JsValue => JsValue = (p: RecoEngPlugin) => JsonServer.jsServer(p.config)
) extends RecoEngApi {
  def server: JsValue => JsValue = serverFactory.apply(plugin)

  implicit val requestHeaderWrites = Writes[JsonRequestHeader] { req =>
    Json.obj(
      "dateTime" -> Json.toJson(req.dateTimeInYyyyMmDd),
      "sequenceNumber" -> Json.toJson(req.sequenceNumber)
    )
  }

  implicit val salesItemWrites = Writes[SalesItem] { it =>
    Json.obj(
      "storeCode" -> Json.toJson(it.storeCode),
      "itemCode" -> Json.toJson(it.itemCode),
      "quantity" -> Json.toJson(it.quantity)
    )
  }

  implicit val onSalesJsonRequestWrites = Writes[OnSalesJsonRequest] { req =>
    Json.obj(
      "header" -> Json.toJson(req.header),
      "transactionMode" -> Json.toJson(req.mode),
      "dateTime" -> Json.toJson(req.tranDateInYyyyMmDd),
      "userCode" -> Json.toJson(req.userCode),
      "itemList" -> Json.toJson(req.itemList)
    )
  }

  implicit val responseHeaderWrites: Writes[JsonResponseHeader] = (
    (__ \ "sequenceNumber").write[String] and
    (__ \ "statusCode").write[String] and
    (__ \ "message").write[String]
  )(unlift(JsonResponseHeader.unapply))
  
  implicit val onSalesResponseWrites = Writes[OnSalesJsonResponse] { resp =>
    Json.obj("header" -> Json.toJson(resp.header))
  }

  implicit val responseHeaderReads: Reads[JsonResponseHeader] = (
    (JsPath \ "sequenceNumber").read[String] and
    (JsPath \ "statusCode").read[String] and
    (JsPath \ "message").read[String]
  )(JsonResponseHeader.apply _)

  implicit val onSalesJsonResponse: Reads[OnSalesJsonResponse] =
    (JsPath \ "header").read[JsonResponseHeader] map OnSalesJsonResponse.apply

  def onSales(
    requestTime: Long = System.currentTimeMillis,
    sequenceNumber: Long = SequenceNumber(),
    transactionMode: TransactionMode,
    transactionTime: Long,
    userCode: String,
    itemTable: Seq[SalesItem]
  ): JsResult[OnSalesJsonResponse] = {
    val req = OnSalesJsonRequest(
      header = JsonRequestHeader(
        dateTime = new DateTime(requestTime),
        sequenceNumber = sequenceNumber.toString
      ),
      mode = TransactionSalesMode.asString,
      dateTime = new DateTime(transactionTime),
      userCode = userCode,
      itemList = itemTable
    )

    server(Json.toJson(req)).validate[OnSalesJsonResponse]
  }
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
