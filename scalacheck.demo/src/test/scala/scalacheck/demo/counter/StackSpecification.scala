package scalacheck.demo.counter


import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.commands.Commands
import org.scalacheck.{Test, Gen, Prop, Properties}

import scala.util.{Success, Try}


object StackSpecification extends Commands {
  override type State = Vector[Int]
  override type Sut = java.util.Stack[Int]

  val commandGen: Gen[Command] = for {
    item <- arbitrary[Int]
    command <- Gen.oneOf(Pop, Push(item))
  } yield command

  override def destroySut(sut: Sut): Unit = ()
  override def initialPreCondition(state: State): Boolean = true
  override def canCreateNewSut(newState: State,
                               initSuts: Traversable[State],
                               runningSuts: Traversable[Sut]): Boolean = true

  override def genInitialState: Gen[State] = Gen.containerOfN[Vector, Int](10, arbitrary[Int])
  override def newSut(state: State): Sut = {
    val stack = new java.util.Stack[Int]()
    state foreach(v => stack.push(v))
    stack
  }
  override def genCommand(state: State): Gen[Command] = commandGen


  case class Push(item: Int) extends UnitCommand {
    def run(sut: Sut): Unit = sut.push(item)
    def nextState(state: State): State = state :+ item
    def preCondition(state: State): Boolean = true
    def postCondition(state: State, success: Boolean): Prop = success
  }

  case object Pop extends Command {
    type Result = Int
    def run(sut: Sut): Result = sut.pop()
    def nextState(state: State): State = state.dropRight(1)
    def preCondition(state: State): Boolean = state.nonEmpty
    override def postCondition(state: Vector[Int], result: Try[Int]): Prop = Success(state.last) == result
  }
}


object StackSpecificationProperties extends Properties("Stack") {

  overrideParameters(Test.Parameters.default.withWorkers(4))

  property("Stack single threaded") = StackSpecification.property()
  property("Stack multithreaded threaded") = StackSpecification.property(threadCount = 2)
}