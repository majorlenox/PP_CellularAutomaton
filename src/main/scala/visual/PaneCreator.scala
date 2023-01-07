package visual

import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.scene.Node
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import structure.Board

object PaneCreator {

  def createBinding(title: String, prop: IntegerProperty, limits: (Int, Int)): Node = {
    val gridPane = new GridPane {
      hgap = 20
      vgap = 10
    }

    val label = Label(title)
    gridPane.add(label, 0, 0)

    val slider = new Slider(limits._1, limits._2, prop.value) {
      minWidth = 300
      maxWidth = 300
      blockIncrement = 1
      majorTickUnit = 1
      snapToTicks = true
    }
    gridPane.add(slider, 0, 1)

    val spinner = new Spinner[Int](limits._1, limits._2, prop.value) {
      minWidth = 100
      maxWidth = 100
    }
    gridPane.add(spinner, 1, 1)

    slider.value onChange {
      (_, _, newValue) => {
        spinner.valueFactory.value.value = newValue.intValue()
        prop.value = newValue.intValue()
      }
    }

    spinner.valueProperty() onChange {
      (_, _, newValue) => {
        if (newValue != slider.value.value) {
          slider.value = newValue
          prop.value = newValue
        }
      }
    }

    gridPane
  }

  def drawBoard(board: Board, size: Int, toColor: Map[Int, Color])(gc: GraphicsContext): Unit = {
    for (x <- 0 to board.cols; y <- 0 to board.rows) {
      gc.fill = toColor(board(x, y))
      gc.fillRect(x * size, y * size, size, size)
    }
  }

}
