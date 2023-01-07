package visual

import scalafx.beans.property.IntegerProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.layout.GridPane

class SizePane(propSeq: Seq[(String, IntegerProperty, (Int, Int))]) {

  def getPane: Node = {
    val gridPane = new GridPane {
      margin = Insets(10)
      vgap = 20
      alignment = Pos.Center
    }

    propSeq.zipWithIndex.foreach {
      case (tuple, i) =>
        val node = PaneCreator.createBinding(tuple._1, tuple._2, tuple._3)
        gridPane.addRow(i, node)
    }

    gridPane
  }

}