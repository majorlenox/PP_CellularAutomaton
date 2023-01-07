package structure

import automaton.{LLRuleOfCA, SRuleOfCA}

object SampleRules {

  def Rule30(): Rule = {
    new SRuleOfCA (2, Map(
      (0, Seq(1, 0, 0)) -> 1,
      (0, Seq(0, 1, 1)) -> 1,
      (0, Seq(0, 1, 0)) -> 1,
      (0, Seq(0, 0, 1)) -> 1,
    ))
  }

  def Rule18(): Rule = {
    new SRuleOfCA(2, Map(
      (0, Seq(1, 0, 0)) -> 1,
      (0, Seq(0, 0, 1)) -> 1,
    ))
  }

  def Rule110(): Rule = {
    new SRuleOfCA(2, Map(
      (0, Seq(1, 1, 0)) -> 1,
      (0, Seq(1, 0, 1)) -> 1,
      (0, Seq(0, 1, 1)) -> 1,
      (0, Seq(0, 1, 0)) -> 1,
      (0, Seq(0, 0, 1)) -> 1,
    ))
  }

  def Rule184(): Rule = {
    new SRuleOfCA(2, Map(
      (0, Seq(1, 1, 1)) -> 1,
      (0, Seq(1, 0, 1)) -> 1,
      (0, Seq(1, 0, 0)) -> 1,
      (0, Seq(0, 1, 1)) -> 1,
    ))
  }

  def Rule90(): Rule = {
    new SRuleOfCA(2, Map(
      (0, Seq(1, 1, 0)) -> 1,
      (0, Seq(1, 0, 0)) -> 1,
      (0, Seq(0, 1, 1)) -> 1,
      (0, Seq(0, 0, 1)) -> 1,
    ))
  }

  def RuleLife(): Rule = {
    new LLRuleOfCA(2, Map(
      (1, Seq(2)) -> 1,
      (1, Seq(3)) -> 1,
      (0, Seq(3)) -> 1,
    ))
  }


}
