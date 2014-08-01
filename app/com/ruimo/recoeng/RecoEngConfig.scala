package com.ruimo.recoeng

import play.api._

case class RecoEngConfig(
  host: String,
  port: Int
) {
  assume(host != null)
}

object RecoEngConfig {
  def get(conf: Configuration): Option[RecoEngConfig] = for {
    host <- conf.getString("recoeng.host")
    port <- conf.getInt("recoeng.port")
  } yield RecoEngConfig(host, port)
}

