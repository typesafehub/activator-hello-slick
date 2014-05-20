import scala.slick.driver.H2Driver.simple._

// Demonstrates various ways of reading data from an Invoker.
object InvokerMethods extends App {

  // A simple dictionary table with keys and values
  class Dict(tag: Tag) extends Table[(Int, String)](tag, "INT_DICT") {
    def key = column[Int]("KEY", O.PrimaryKey)
    def value = column[String]("VALUE")
    def * = (key, value)
  }
  val dict = TableQuery[Dict]

  val db = Database.forURL("jdbc:h2:mem:invoker", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the dictionary table and insert some data
    dict.ddl.create
    dict ++= Seq(1 -> "a", 2 -> "b", 3 -> "c", 4 -> "d", 5 -> "e")

    // Define a pre-compiled parameterized query for reading all key/value
    // pairs up to a given key.
    val upTo = Compiled { k: Column[Int] =>
      dict.filter(_.key <= k).sortBy(_.key)
    }

    println("List of k/v pairs up to 3 with .list")
    println("- " + upTo(3).list)

    println("IndexedSeq of k/v pairs up to 3 with .buildColl")
    println("- " + upTo(3).buildColl[IndexedSeq])

    println("Set of k/v pairs up to 3 with .buildColl")
    println("- " + upTo(3).buildColl[Set])

    println("Array of k/v pairs up to 3 with .buildColl")
    println("- " + upTo(3).buildColl[Array])

    println("All keys in an unboxed Array[Int]")
    val allKeys = dict.map(_.key)
    println("  " + allKeys.buildColl[Array])

    println("Stream k/v pairs up to 3 via an Iterator")
    val it = upTo(3).iterator
    try {
      it.foreach { case (k, v) => println(s"- $k -> $v") }
    } finally {
      // Make sure to close the Iterator in case of an error. (It is
      // automatically closed when all data has been read.)
      it.close
    }

    println("Only get the first result, failing if there is none")
    println("- " + upTo(3).first)

    println("Get the first result as an Option, or None")
    println("- " + upTo(3).firstOption)

    println("Map of k/v pairs up to 3 with .toMap")
    println("- " + upTo(3).toMap)

    println("Combine the k/v pairs up to 3 with .foldLeft")
    println("- " + upTo(3).foldLeft("") { case (z, (k, v)) => s"$z[$k -> $v] " })
  }
}
