package visual

import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, ComboBox, Label, TreeTableRow}
import scalafx.scene.layout.GridPane
import structure.{Neighbourhood, Rule}

class RulesPane(
  rule:          ObjectProperty[Rule],
) {

  def getPane(cs: Canvas): Node = {
    val gridPane = new GridPane {
      hgap = 20
      vgap = 10
      alignment = Pos.Center
    }

    val ruleMapping = new Label("Mapping") {
      minWidth = 130
      maxWidth = 130
    }
    gridPane.add(ruleMapping, 0, 0)

    val mapPane = new GridPane {
      hgap = 20
      vgap = 10
      alignment = Pos.BaselineLeft
    }

    val plusButton = new Button("+") {
      minWidth = 50
      maxWidth = 50

      // onAction = (_: ActionEvent) => tabPane
    }

    val minusButton = new Button("-") {
      minWidth = 50
      maxWidth = 50

      //onAction = (_: ActionEvent) => removeItem
    }

    mapPane.add(minusButton, 1, 0)
    mapPane.add(plusButton, 1, 1)


    gridPane.add(mapPane, 0, 1)

    val neighbourhood = new Label("Neighbourhood") {
      minWidth = 130
      maxWidth = 130
    }
    gridPane.add(neighbourhood, 1, 0)

    gridPane.add(cs, 1, 1)

    gridPane
  }

}
