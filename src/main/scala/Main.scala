// based on Life-like cellular automaton by ItsNotAUsername
// https://github.com/ItsNotAUsername/scala-cellular-automata

import automaton.ControllerOfCA
import scalafx.application.JFXApp

object Main extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "CellularAutomation"
    scene = ControllerOfCA.createScene()
  }
}
