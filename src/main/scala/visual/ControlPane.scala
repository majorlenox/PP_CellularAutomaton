package visual

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label, ToggleButton}
import scalafx.scene.layout.GridPane

class ControlPane(
                    onStart:     => Unit,
                    onNext:      => Unit,
                    onClear:     => Unit,
                    onRandom:    => Unit,
                    step:        IntegerProperty,
                    speed:       IntegerProperty,
                    speedLimits: (Int, Int)
) {

  def getPane: Node = {
    val gridPane = new GridPane {
      margin = Insets(20)
      hgap = 25
      vgap = 20
      alignment = Pos.Center
    }

    val stepLabel = Label("Step: 0")
    gridPane.add(stepLabel, 0, 0, 3, 1)

    step onChange {
      (_, _, newValue) => stepLabel.text = "Step: " + newValue.toString
    }

    val bind = PaneCreator.createBinding("Speed", speed, speedLimits)
    gridPane.add(bind, 0, 1, 3, 1)

    val startButton = new ToggleButton("Start") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => onStart
    }
    gridPane.add(startButton, 0, 2)

    val nextButton = new Button("Next") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => onNext
    }
    gridPane.add(nextButton, 1, 2)

    val clearButton = new Button("Clear") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => {
        if (startButton.selected.value) {
          startButton.fire()
        }
        onClear
      }
    }
    gridPane.add(clearButton, 2, 2)

    val randomButton = new Button("Random") {
      minWidth = 125
      maxWidth = 125

      onAction = (_: ActionEvent) => {
        if (startButton.selected.value) {
          startButton.fire()
        }
        onRandom
      }
    }
    gridPane.add(randomButton, 3, 2)

    gridPane
  }

}
