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
import com.ruimo.recoeng.json.RecommendBySingleItemJsonResponse
import com.ruimo.recoeng.json.Desc
import com.ruimo.recoeng.json.JsonRequestPaging
import play.api.libs.json._
import Helper._
import Json.toJson

@RunWith(classOf[JUnitRunner])
class RecoEngPluginSpec extends Specification {
  "RecoEngPlugin" should {
    "onSales should issue valid request and accept response" in {
      val pseudoServer: JsValue => JsValue = { (jsReq: JsValue) =>
        doWith(jsReq \ "header") { header =>
          header \ "dateTime" === toJson("20140102123456")
          header \ "sequenceNumber" === toJson("12345")
        }
        jsReq \ "transactionMode" === toJson(TransactionSalesMode.asString)
        jsReq \ "dateTime" === toJson("20140202013412")
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
      val api = new RecoEngApiImpl(mock(classOf[RecoEngPlugin]), plugin => pseudoServer)
      val resp: JsResult[OnSalesJsonResponse] = api.onSales(
        Formatters.YyyyMmDdHhMmSsFormat.parseDateTime("20140102123456").getMillis,
        12345L,
        TransactionSalesMode,
        Formatters.YyyyMmDdHhMmSsFormat.parseDateTime("20140202013412").getMillis,
        "user001",
        Seq(
          SalesItem("store01", "item01", 1),
          SalesItem("store02", "item02", 4)
        )
      )

      doWith(resp.get) { onSalesJsonResponse =>
        doWith(onSalesJsonResponse.header) { header =>
          header.sequenceNumber === "3194710"
          header.statusCode === "OK"
          header.message === "msg"
        }
      }
    }

    "recommendBySingleItem should issue valid request and accept response" in {
      val pseudoServer: JsValue => JsValue = { (jsReq: JsValue) =>
        doWith(jsReq \ "header") { header =>
          header \ "dateTime" === toJson("20140102123456")
          header \ "sequenceNumber" === toJson("12345")
        }
        jsReq \ "storeCode" === toJson("111111")
        jsReq \ "itemCode" === toJson("222222")
        jsReq \ "sort" === toJson("desc(score)")
        doWith(jsReq \ "paging") { paging =>
          paging \ "offset" === toJson(1)
          paging \ "limit" === toJson(10)
        }

        Json.parse(
          """
          {
            "header": {
              "sequenceNumber": "3194710",
              "statusCode": "OK",
              "message": "msg"
            },
            "itemList": [
              {
                "storeCode": "4",
                "itemCode": "20481",
                "score": 12
              },
              {
                "storeCode": "2",
                "itemCode": "2044454",
                "score": 4
              }
            ],
            "sort": "desc(score)",
            "paging": {
              "offset": 0,
              "limit": 10
            }
          }
          """
        )        
      }
      val api = new RecoEngApiImpl(mock(classOf[RecoEngPlugin]), plugin => pseudoServer)
      val resp: JsResult[RecommendBySingleItemJsonResponse] = api.recommendBySingleItem(
        requestTime = Formatters.YyyyMmDdHhMmSsFormat.parseDateTime("20140102123456").getMillis,
        sequenceNumber = 12345L,
        storeCode = "111111",
        itemCode = "222222",
        sort = Desc("score"),
        paging = JsonRequestPaging(
          offset = 1,
          limit = 10
        )
      )

      doWith(resp.get) { recommendResp =>
        doWith(recommendResp.header) { header =>
          header.sequenceNumber === "3194710"
          header.statusCode === "OK"
          header.message === "msg"
        }
        doWith(recommendResp.itemList) { itemList =>
          itemList.size === 2
          doWith(itemList(0)) { item =>
            item.storeCode === "4"
            item.itemCode === "20481"
            item.score === 12f
          }
          doWith(itemList(1)) { item =>
            item.storeCode === "2"
            item.itemCode === "2044454"
            item.score === 4f
          }
        }
        recommendResp.sort === "desc(score)"
        doWith(recommendResp.paging) { paging =>
          paging.offset === 0f
          paging.limit === 10
        }
      }
    }
  }
}
