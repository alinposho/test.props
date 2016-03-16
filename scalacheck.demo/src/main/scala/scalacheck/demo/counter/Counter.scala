package scalacheck.demo.counter

class Counter {
  private var n: Double = 0
  def inc = {
    n = n + 1
    n
  }
  def dec = {
    n = n - 1
    n
  }
//  def dec = if(n > 3) n -= 2 else n -= 1  // Bug!
  def get: Double = n
  def reset = n = 0
}

