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
    val results = for(_ <- 1 to 100000) yield counterDecrementRaceConditionTest()
    val successes = results.filter(_ < 0)
    assert(successes.nonEmpty)
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
