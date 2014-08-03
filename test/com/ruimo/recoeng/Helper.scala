package com.ruimo.recoeng

object Helper {
  def doWith[T, R](data: T)(func: T => R): R = func(data)
}
