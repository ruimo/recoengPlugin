package com.ruimo.recoeng

import play.api._

class RecoEngPlugin extends Plugin {
  override def onStart() {
    println("Plugin started...")
  }

  override def onStop() {
    println("Plugin stopped...")
  }
}
