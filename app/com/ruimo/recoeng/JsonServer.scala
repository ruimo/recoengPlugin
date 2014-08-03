package com.ruimo.recoeng

import play.api.libs.json._
import scala.concurrent.Await
import play.api.libs.ws.WS
import scala.concurrent.duration._
import play.api.mvc.Results
import com.ruimo.recoeng.json.OnSalesJsonResponse
import com.ruimo.recoeng.json.JsonResponseHeader

object JsonServer {
  def jsServer(configOpt: Option[RecoEngConfig])(req: JsValue): JsValue =
    configOpt.map { config =>
      val url = "http://" + config.host + ":" + config.port + "/onSales"
      val resp = Await.result(
        WS.url(url)
          .withHeaders("Content-Type" -> "application/json; charset=utf-8")
          .post(req), Duration(30, SECONDS)
      )

      assume(resp.status == Results.Ok.header.status, "Status invalid (=" + resp.status)
      Json.parse(resp.body)
    }.getOrElse {
      val seqNo = (req \ "header" \ "sequenceNumber").as[String] 
      Json.parse(
        s"""
        {
          "header": {
            "sequenceNumber": "$seqNo",
            "statusCode" = "OK",
            "message" = "No Redis settings found. This is subbed response"
          }
        }
        """
      )
    }
}
