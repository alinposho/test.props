import scalacheck.demo.counter.CounterSpecification

CounterSpecification.property(threadCount = 3).
  check(_.withMinSize(100).withWorkers(4))