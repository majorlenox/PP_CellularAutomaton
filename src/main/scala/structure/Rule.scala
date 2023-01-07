package structure

trait Rule {

  def transform(cell: Int, neighbours: Seq[Int]): Int

}