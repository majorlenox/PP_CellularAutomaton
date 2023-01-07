package structure

import scala.{+:, :+}
import scala.annotation.tailrec
import scala.collection.immutable.Nil.++
import scala.collection.immutable._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class Board private(val cells: Vector[Vector[Int]]) {

  val rows: Int = cells.length

  val cols: Int = cells.head.length

  def apply(x: Int, y: Int): Int = cellByIndex(x, y)

  def next(rule: Rule, nb: Neighbourhood): Board = {
    val newCells = Vector.tabulate(rows, cols)(
      (y, x) => {
        val neighbours = nb.neighbours(x, y).map {
          case (i, j) => cellByIndex(i, j)
        }
        rule.transform(cells(y)(x), neighbours)
      }
    )
    new Board(newCells)
  }


  /* threads
def next(rule: Rule, nb: Neighbourhood, numThreads: Int): Board = {
  val tRows = rows / numThreads
  val tCols = cols
  val threads = for(i <- 0 until numThreads) yield new Thread {
    override def run(): Vector[Vector[Int]] = {
      Vector.tabulate(tRows, tCols)(
        (y, x) => {
              val neighbours = nb.neighbours(x, y + i * tRows).map {
                case (k, r) => cellByIndex(k, r)
              }
              rule.transform(cells(y + i * tRows)(x), neighbours)
        }
      )
    }
  }
  threads.foreach(_.start)
  threads.foreach(_.join)

  new Board(newCells)
}
   */

  def next(rule: Rule, nb: Neighbourhood, numThreads: Int): Board = {
    val tRows = rows / numThreads
    val tCols = cols
    val newRows = for (i <- 0 until numThreads) yield Future {
        Vector.tabulate(tRows, tCols)(
          (y, x) => {
            val neighbours = nb.neighbours(x, y + i * tRows).map {
              case (k, r) => cellByIndex(k, r)
            }
            rule.transform(cells(y + i * tRows)(x), neighbours)
          }
        )
    }

    newRows.map(Await.result(_, Duration.Inf))
    val newVectors = newRows.map(_.value).map { case Some(Success(x)) => x}

    val newCells = (for {
      i <- newVectors
      j <- i
    } yield j).appendedAll(
    Vector.tabulate(rows - tRows * numThreads, tCols)(
      (y, x) => {
        val neighbours = nb.neighbours(x, y + numThreads * tRows).map {
          case (k, r) => cellByIndex(k, r)
        }
        rule.transform(cells(y + numThreads * tRows)(x), neighbours)
      }
    ))


    new Board(newCells.toVector)
  }

  def copy(x: Int, y: Int, newCell: Int): Board = {
    val (xb, yb) = bounds(x, y)
    val newCells = cells.updated(yb, cells(yb).updated(xb, newCell))
    new Board(newCells)
  }

  private def cellByIndex(x: Int, y: Int): Int = {
    val (xb, yb) = bounds(x, y)
    cells(yb)(xb)
  }

  @tailrec
  private def bounds(x: Int, y: Int): (Int, Int) =
    if (x >= cols)
      bounds(x - cols, y)
    else if (x < 0)
      bounds(x + cols, y)
    else if (y >= rows)
      bounds(x, y - rows)
    else if (y < 0)
      bounds(x, y + rows)
    else
      (x, y)

}

object Board {

  def fill(rows: Int, cols: Int, cell: Int): Board = {
    val cells = Vector.fill(rows, cols)(cell)
    new Board(cells)
  }

  def random (rows: Int, cols: Int, nColors: Int, dim: Boolean): Board = {
    val random = scala.util.Random
    val k = scala.util.Random.between(1, 60)
    val cells = Vector.fill(rows, cols)(
      if (random.nextInt(100) < k) {
        random.between(1, nColors)
      } else 0
    )
    if (!dim){
      new Board(Vector.fill(rows-1, cols)(0).prepended(cells.head))
    }else
      new Board(cells)
  }

  def boardToNeighbourhood(board: Board): Neighbourhood = {
    val x1 = Neighbourhood.columns / 2
    val y1 = Neighbourhood.rows / 2
    val nSeq = for {
      i <- 0 until Neighbourhood.rows
      j <- 0 until Neighbourhood.columns
      if ((j != x1) || (i != y1)) && (board.cells(i)(j) == 1)
    } yield {
      (-x1 + j, -y1 + i)
    }
    new Neighbourhood(nSeq)
    }

  def mooreNeighbourhood(rows: Int, cols: Int, k: Int): Board = {
    val x1 = cols / 2
    val y1 = rows / 2
    val cells = Vector.tabulate(rows, cols){
      (i,j) => if ( (math.abs(i - y1) <= k) && (math.abs(j - x1) <= k)) {
        if ((i==y1) && (j==x1)){2}else{1}} else {0}
    }
    new Board(cells)
  }

}