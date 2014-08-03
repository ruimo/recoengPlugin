package com.ruimo.recoeng

import play.api.libs.json._
import org.specs2.mutable.Specification
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

@RunWith(classOf[JUnitRunner])
class JsonServerSpec extends Specification {
  "JsonServer" should {
    "noConfigError should properly created" in {
      val err = JsonServer.noConfigError(
        Json.parse(
          """
          {
            "header": {
              "sequenceNumber": "12345"
            }
          }
          """
        )
      )

      (err \ "header" \ "sequenceNumber").as[String] === "12345"
      (err \ "header" \ "statusCode").as[String] === "IG"
    }
  }
}
