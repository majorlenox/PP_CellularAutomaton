package controller

import scalafx.event.ActionEvent
import scalafx.Includes._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.control.{Label, ToggleButton}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import structure.{Board, Neighbourhood, Rule}
import visual.{ColorsPane, ControlPane, PaneCreator, RulesPane, SizePane}
import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.beans.property.{BooleanProperty, IntegerProperty, ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{ScrollPane, TitledPane}
import scalafx.scene.effect.{BlurType, DropShadow}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, Priority, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.{Node, Scene}
import scalafx.util.Duration
import scalafx.Includes._
import scalafx.beans.property.IntegerProperty
import scalafx.scene.Node
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.{Label, Slider, Spinner}
import scalafx.scene.layout.GridPane
import scalafx.scene.paint.Color
import structure.Board.{boardToNeighbourhood, random}


class vController(
                   initNColors:       Int,
                   initRule:          Rule,
                   initRandomDim:     Boolean,
                   numThreads:        Int
                                 ) {

  /** Board and cell size */
  private val rows = IntegerProperty(20)
  private val cols = IntegerProperty(20)
  private val cellSize = IntegerProperty(20)

  /** Canvas for drawing cells */
  private val cellCanvas = new Canvas {
    width = cols.value * cellSize.value
    height = rows.value * cellSize.value

    effect = new DropShadow {
      blurType = BlurType.Gaussian
      radius = 25
      color = Color.Black
    }
  }

  /** Canvas for neighbourhood */
  private val neighbourhoodCanvas = new Canvas {
    width = Neighbourhood.columns * 13
    height = Neighbourhood.rows * 13

    effect = new DropShadow {
      blurType = BlurType.Gaussian
      radius = 5
      color = Color.Black
    }
  }



  private val colors = Seq(Color.White, Color.Blue, Color.Orange, Color.Brown, Color.Gold, Color.Pink, Color.Grey,
    Color.Khaki)
  private val colorsMap = (for {
    i <- colors.indices
  } yield i -> colors(i)).toMap

  private val gc = cellCanvas.graphicsContext2D
  private val nh = neighbourhoodCanvas.graphicsContext2D

  /** Simulation properties */
  private val step = IntegerProperty(0)
  private val speed = IntegerProperty(20) // 20
  private val nColors = IntegerProperty(initNColors)
  private val running = BooleanProperty(false)

  /** Drawing properties */
  private val chosenCell = ObjectProperty(0)
  private val toColor = ObjectProperty(colorsMap)

  /** Cellular automaton's properties */
  protected val rule: ObjectProperty[Rule] = ObjectProperty(initRule)

  /** Board */
  private var board = Board.fill(rows.value, cols.value, 0)

  private var nhBoard = Board.mooreNeighbourhood(Neighbourhood.rows, Neighbourhood.columns, 1)
  private var neighbourhood = boardToNeighbourhood(nhBoard)

  /** Timeline */
  private val timeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = KeyFrame(
      time = Duration(1000 / speed.value),
      onFinished = (_: ActionEvent) => updateBoard()
    )
  }

  /** PaneViews */
  private val controlPaneView = new ControlPane(
    onStart(),
    onNext(),
    onClear(),
    onRandom(),
    step,
    speed,
    (1, 40)
  )
  private val sizePaneView = new SizePane(
    Seq(
      ("Rows", rows, (1, 500)),
      ("Columns", cols, (1, 500)),
      ("Cell size", cellSize, (1, 30))
    )
  )
  private val colorPaneView = new ColorsPane(nColors, chosenCell, toColor, (2, colors.size))
  private val rulePaneView = new RulesPane(rule)

  def createScene(): Scene = {
    val controlPane = controlPaneView.getPane
    val colorPane = createPane("Draw", colorPaneView.getPane)
    val rulePane = createPane("Rule", rulePaneView.getPane(neighbourhoodCanvas), disableOnRunning = true)
    val sizePane = createPane("Grid", sizePaneView.getPane, disableOnRunning = true)

    val dashboard = new VBox {
      alignment = Pos.TopCenter
      children = Seq(controlPane, colorPane, rulePane, sizePane)
    }

    val sceneContent = new HBox {
      padding = Insets(25)
      spacing = 25
      children = Seq(dashboard, cellCanvas)
    }

    initEventHandlers()
    drawBoard()
    drawNeighbourhood()


    new Scene {
      //stylesheets += getClass.getResource("/css/styles.css").toExternalForm

      root = new ScrollPane {
        vgrow = Priority.Always
        hgrow = Priority.Always
        fitToHeight = true
        fitToWidth = true
        content = sceneContent
      }
    }
  }

  private def createPane(title: String, node: Node, disableOnRunning: Boolean = false): TitledPane =
    new TitledPane {
      text = title
      content = node
      if (disableOnRunning) {
        node.disable <== running
      }
    }

  /** Event handlers */
  private def initEventHandlers(): Unit = {
    rows onChange {
      (_, _, newValue) => {
        cellCanvas.height = cellSize.value * newValue.doubleValue()
        updateBoardSize()
      }
    }
    cols onChange {
      (_, _, newValue) => {
        cellCanvas.width = cellSize.value * newValue.doubleValue()
        updateBoardSize()
      }
    }
    cellSize onChange {
      (_, _, newValue) => {
        cellCanvas.width = cols.value * newValue.doubleValue()
        cellCanvas.height = rows.value * newValue.doubleValue()
        drawBoard()
      }
    }
    speed onChange {
      (_, _, newValue) => timeline.rate = newValue.doubleValue() / 20
    }
    toColor onChange {
      drawBoard()
    }

  /*  nColors onChange {
      (_, _, newValue) =>
      // clear rule

    }*/

    // Mouse events
    val boardEventHandler = (event: MouseEvent) => if (!running.value) {
      val x = event.x.toInt / cellSize.value
      val y = event.y.toInt / cellSize.value
      if (x < cols.value && x >= 0 && y < rows.value && y >= 0) {
        board = board.copy(x, y, chosenCell.value)
        drawBoard()
      }
    }
    cellCanvas.onMouseClicked = boardEventHandler
    cellCanvas.onMouseDragged = boardEventHandler

    val NeighbourhoodEventHandler = (event: MouseEvent) => if (!running.value) {
      val x = event.x.toInt / 13
      val y = event.y.toInt / 13
      if ((x < Neighbourhood.columns && x >= 0 && y < Neighbourhood.rows && y >= 0) &&
        ((x != Neighbourhood.columns/2) || (y != Neighbourhood.rows/2))) {
        nhBoard = nhBoard.copy(x, y, (nhBoard.cells(y)(x) + 1) % 2)
        drawNeighbourhood()
      }
    }
    neighbourhoodCanvas.onMouseClicked = NeighbourhoodEventHandler

  }

  /** Callbacks */
  private def onStart(): Unit = {
    if (running.value) {
      running.value = false
      timeline.pause()
    } else {
      running.value = true
      timeline.play()
    }
  }

  private def onNext(): Unit = {
    if (running.value) {
      running.value = false
      timeline.pause()
    }
    updateBoard()
  }

  private def onClear(): Unit = {
    if (running.value) {
      running.value = false
    }
    board = Board.fill(rows.value, cols.value, 0)
    step.value = 0
    drawBoard()
  }

  private def onRandom(): Unit = {
    if (running.value) {
      running.value = false
    }
    board = Board.random(rows.value, cols.value, nColors.value, initRandomDim)
    step.value = 0
    drawBoard()
  }

  private def updateBoard(): Unit = {
    board = board.next(rule.value, neighbourhood, numThreads) // parallel
    // board = board.next(rule.value, neighbourhood) // no parallel
    //if (step.value == 0) {
    //  println(java.time.Instant.now.toEpochMilli)
    //}
    step.value = step.value + 1
    //if (step.value == 500)
    //  println(java.time.Instant.now.toEpochMilli)
    drawBoard()
  }

  private def updateBoardSize(): Unit = {
    board = Board.fill(rows.value, cols.value, 0)
    drawBoard()
  }

  private def drawBoard(): Unit = {
    PaneCreator.drawBoard(board, cellSize.value, toColor.value)(gc)
  }

  private def drawNeighbourhood(): Unit = {
    PaneCreator.drawBoard(nhBoard, 13, Map(0 -> Color.White, 1 -> Color.Black, 2 -> Color.Red))(nh)
    neighbourhood = boardToNeighbourhood(nhBoard)
  }

}