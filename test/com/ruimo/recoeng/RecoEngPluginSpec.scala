package com.ruimo.recoeng

import org.specs2.mutable.Specification
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.mockito.Mockito.mock
import com.ruimo.recoeng.json.Formatters
import com.ruimo.recoeng.json.TransactionSalesMode
import com.ruimo.recoeng.json.SalesItem
import com.ruimo.recoeng.json.OnSalesJsonResponse
import play.api.libs.json._
import Helper._
import Json.toJson

@RunWith(classOf[JUnitRunner])
class RecoEngPluginSpec extends Specification {
  "RecoEngPlugin" should {
    "onSales should issue valid request and accept response" in {
      val api = new RecoEngApiImpl(mock(classOf[RecoEngPlugin]))
      val resp: JsResult[OnSalesJsonResponse] = api.onSales(
        Formatters.YyyyMmDdFormat.parseDateTime("20140102").getMillis,
        12345L,
        TransactionSalesMode,
        Formatters.YyyyMmDdFormat.parseDateTime("20140202").getMillis,
        "user001",
        Seq(
          SalesItem("store01", "item01", 1),
          SalesItem("store02", "item02", 4)
        )
      ) { (jsReq: JsValue) =>
        doWith(jsReq \ "header") { header =>
          header \ "dateTime" === toJson(20140102)
          header \ "sequenceNumber" === toJson("12345")
        }
        jsReq \ "transactionMode" === toJson(TransactionSalesMode.asString)
        jsReq \ "dateTime" === toJson(20140202)
        jsReq \ "userCode" === toJson("user001")
        doWith(jsReq \ "itemList") { items =>
          val ary = items.asInstanceOf[JsArray]
          ary.value.size === 2
          doWith(ary.value(0)) { item =>
            item \ "storeCode" === toJson("store01")
            item \ "itemCode" === toJson("item01")
            item \ "quantity" === toJson(1)
          }
          doWith(ary.value(1)) { item =>
            item \ "storeCode" === toJson("store02")
            item \ "itemCode" === toJson("item02")
            item \ "quantity" === toJson(4)
          }
        }

        Json.parse(
          """
          {
            "header": {
              "sequenceNumber": "3194710",
              "statusCode": "OK",
              "message": "msg"
            }
          }
          """
        )        
      }

      doWith(resp.get) { onSalesJsonResponse =>
        doWith(onSalesJsonResponse.header) { header =>
          header.sequenceNumber === "3194710"
          header.statusCode === "OK"
          header.message === "msg"
        }
      }
    }
  }
}
