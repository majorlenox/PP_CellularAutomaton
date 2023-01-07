package visual

import javafx.collections.ObservableList
import scalafx.Includes._
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.beans.value.ObservableValue
import scalafx.event.ActionEvent
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.{ColorPicker, ComboBox, Label, Spinner}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color

class ColorsPane(
              nColors:    IntegerProperty,
              chosenCell: ObjectProperty[Int],
              toColor:    ObjectProperty[Map[Int, Color]],
              limits:     (Int, Int)
) {

  def getPane: Node = {
    val gridPane = new GridPane {
      hgap = 20
      alignment = Pos.Center
    }

    val numberOfColorsLabel = new Label("Number Of Colors") {
      minWidth = 130
      maxWidth = 130
    }
    gridPane.add(numberOfColorsLabel, 0, 0)

    val spinner = new Spinner[Int](limits._1, limits._2, 2) {
      minWidth = 70
      maxWidth = 70
    }
    gridPane.add(spinner, 1, 0)

    val cellLabel = new Label("Chosen cell") {
      minWidth = 90
      maxWidth = 90
    }
    gridPane.add(cellLabel, 0, 1)

    val colorPicker = new ColorPicker(toColor.value(0)) {
      minWidth = 100
      maxWidth = 100

      onAction = (_: ActionEvent) =>
        toColor.value = toColor.value + (chosenCell.value -> value.value)
    }
    gridPane.add(colorPicker, 2, 1)

    val colors = (0 until nColors.value).toList

    val comboBox = new ComboBox[Int](colors) {
      minWidth = 100
      maxWidth = 100

      value = 0

      value onChange {
        (_, _, newValue) => {
          chosenCell.value = newValue
          colorPicker.value = toColor.value(newValue)
        }
      }
    }
    gridPane.add(comboBox, 1, 1)

    spinner.valueProperty() onChange {
      (_, _, newValue) => {
        if (newValue != nColors.value) {
          nColors.value = newValue
          comboBox.items.value.clear()
          comboBox.items.value.addAll((0 until nColors.value).toList)
          chosenCell.value = 0
        }
      }
    }

    gridPane
  }

}
