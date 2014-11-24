package com.ruimo.recoeng

import play.api.Logger
import play.api.libs.json._
import scala.concurrent.Await
import play.api.libs.ws.WS
import scala.concurrent.duration._
import play.api.mvc.Results
import com.ruimo.recoeng.json.OnSalesJsonResponse
import com.ruimo.recoeng.json.JsonResponseHeader
import play.api.Play.current

object JsonServer {
  val logger = Logger(getClass)

  def jsServer(configOpt: Option[RecoEngConfig])(contextPath: String, req: JsValue): JsValue = {
    logger.debug("Sending recommend request: " + req)

    configOpt.map { config =>
      val url = "http://" + config.host + ":" + config.port + contextPath
      val resp = Await.result(
        WS.url(url)
          .withHeaders("Content-Type" -> "application/json; charset=utf-8")
          .post(req), Duration(30, SECONDS)
      )

      assert(
        resp.status == Results.Ok.header.status,
        "Status invalid (=" + resp.status + ") request: " + req
      )
      val jsResp = Json.parse(resp.body)

      logger.debug("Received recommend response: " + jsResp)
      jsResp
    }.getOrElse(noConfigError(req))
  }

  def noConfigError(req: JsValue): JsValue = noConfigError(
    (req \ "header" \ "sequenceNumber").as[String]
  )

  def noConfigError(seqNo: String): JsValue = Json.parse(
    s"""
    {
      "header": {
        "sequenceNumber": "$seqNo",
        "statusCode": "IG",
        "message": "No Redis settings found. This request is just ignored."
      }
    }
    """
  )
}
