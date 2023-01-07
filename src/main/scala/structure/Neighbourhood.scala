package structure

class Neighbourhood(nSeq: Seq[(Int, Int)]) {

  def neighbours(x: Int, y: Int): Seq[(Int, Int)] = {
    for {
      i <- nSeq
    } yield {
      (x + i._1, y + i._2)
    }
  }

}

object Neighbourhood {
  val rows = 9
  val columns = 9
}