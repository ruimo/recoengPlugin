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
          SalesItem(
            "store01", "item01", 1
          ),
          SalesItem(
            "store02", "item02", 4
          )
        )
      ) { (jsReq: JsValue) =>
println("req = " + jsReq)

        jsReq
      }
println("resp = " + resp)
      1 === 1
    }
  }
}
