package scalacheck.demo.counter

class PositiveCounter(private var n: Double = 0) {
  def inc = n += 1
  def dec = if(n > 0) n -= 1 else n
  def get: Double = n
  def reset = n = 0
}
