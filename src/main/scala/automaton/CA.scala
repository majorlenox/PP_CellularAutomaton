package automaton

import controller.vController
import structure.Rule
import structure.SampleRules.{Rule110, Rule18, Rule184, Rule30, Rule90, RuleLife}


class LLRuleOfCA(nColors: Int, mapping: Map[(Int, Seq[Int]), Int]) extends Rule {

  def function(cell: Int, nCells: Seq[Int]): Int = {
    if (!mapping.contains(cell, nCells.tail))
      0
    else
      mapping(cell, nCells.tail)
  }
  
  override def transform(cell: Int, neighbours: Seq[Int]): Int = {
    val colors = (0 until nColors).toList
    function(cell, colors.map(color => neighbours.count(_ == color)))
  }

}

class SRuleOfCA(nColors: Int, mapping: Map[(Int, Seq[Int]), Int]) extends Rule {

  override def transform(cell: Int, neighbours: Seq[Int]): Int = {
    if (!mapping.contains(cell, neighbours))
      cell
    else
      mapping(cell, neighbours)
  }

}

// RuleLife()
// Rule184()
// Rule110()
// Rule90()
// Rule30()
// Rule18()
object ControllerOfCA extends vController (2, RuleLife(), true, 25)
// 500, 500, step 500, cellSize = 4, Without Visual, Speed = 1000, Rule90
// no parallel  -> 31.4, 33.8 seconds
// 1 thread     -> 28.14 seconds
// 2 threads    -> 15.3 seconds
// 10 threads   -> 7.27, 6.4 seconds
// 25 threads   -> 6.8, 6.9 seconds
// 50 threads   -> 6.2, 5.8 seconds
