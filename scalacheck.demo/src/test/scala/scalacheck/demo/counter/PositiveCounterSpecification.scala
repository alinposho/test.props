package scalacheck.demo.counter

import org.scalacheck.commands.Commands
import org.scalacheck.{Properties, Gen, Prop}
import scala.util.{Try, Success}

object PositiveCounterSpecification extends Commands {

  type State = Int
  type Sut = PositiveCounter

  def canCreateNewSut(newState: State, initSuts: Traversable[State],
                      runningSuts: Traversable[Sut]): Boolean = true

  def initialPreCondition(state: State): Boolean = state == 0

  def newSut(state: State): Sut = new PositiveCounter()

  def destroySut(sut: Sut): Unit = ()

  def genInitialState: Gen[State] = Gen.const(0)

  def genCommand(state: State): Gen[Command] = Gen.oneOf(
    Inc, Get, Dec, Reset
  )

  case object Inc extends UnitCommand {
    def run(sut: Sut): Unit = sut.inc

    def nextState(state: State): State = state + 1

    def preCondition(state: State): Boolean = true

    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Dec extends UnitCommand {
    def run(sut: Sut): Unit = sut.dec

    def nextState(state: State): State = if (state > 0) state - 1 else state

    def preCondition(state: State): Boolean = true

    def postCondition(state: State, success: Boolean): Prop = state >= 0 && success
  }

  case object Reset extends UnitCommand {
    def run(sut: Sut): Unit = sut.reset

    def nextState(state: State): State = 0

    def preCondition(state: State): Boolean = true

    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Get extends Command {
    type Result = Double

    def run(sut: Sut): Result = sut.get

    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = true

    def postCondition(state: State, result: Try[Result]): Prop = result == Success(state)
  }

}

object PositiveCounterSpecificationProperties extends Properties("positive-counter") {
  property("PositiveCounter single threaded") = PositiveCounterSpecification.property()
  // This should fail but it doesn't
  property("PositiveCounter multithreaded threaded") = PositiveCounterSpecification.property(threadCount = 4)

  scalacheck.comment {
    // Run this in the Scala console
    //import scalacheck.demo.counter.PositiveCounterSpecification
    PositiveCounterSpecification.property().check
    PositiveCounterSpecification.property(2).check(_.withWorkers(4).withMinSuccessfulTests(1000))
  }
}





