package scalacheck.demo.counter

import java.util.concurrent.{TimeUnit, CountDownLatch}

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.tagobjects.Retryable
import org.scalatest.{Retries, Assertions, FlatSpec, Matchers}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class PositiveCounterRaceConditionSpec extends FlatSpec
  with Assertions
  with Matchers
  with ScalaFutures {


  "PositiveInteger" should "have a race condition when decrementing" in {
    val triesUntilRaceCondition = Future {
      var tries = 0
      while (counterDecrementRaceConditionTest() >= 0) {
        tries += 1
        // Introduce a small delay
        Thread.sleep(100)
      }
      tries
    }

    val tries = Await.result(triesUntilRaceCondition, 1.minute)
    assert(tries > 0)
    print(s"Tries until race condition=$tries")
  }

  def counterDecrementRaceConditionTest(): Double = {
    // Prepare
    val counter = new PositiveCounter(1)
    val startSignal = new CountDownLatch(1)

    // Exercise
    val futures = for (_ <- 1 to 4) yield Future {
      startSignal.await(10, TimeUnit.SECONDS)
      counter.dec
      counter.inc
      counter.dec
    }
    startSignal.countDown()
    Await.ready(Future.sequence(futures), 10.seconds)

    counter.get
  }
}
