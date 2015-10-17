import slick.driver.H2Driver.api._
import slick.driver.H2Driver.api.Database
import scala.concurrent.duration._


import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

// The main application
object HelloSlick extends App {

  // The query interface for the Suppliers table
  val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

  // the query interface for the Coffees table
  val coffees: TableQuery[Coffees] = TableQuery[Coffees]
  //
  //  // Create a connection (called a "session") to an in-memory H2 database

  // Create a connection (called a "session") to an in-memory H2 database
  val db = Database.forConfig("h2mem1")

  val setup = DBIO.seq(
    // Create the tables, including primary and foreign keys
    (suppliers.schema ++ coffees.schema).create,

    // Insert some suppliers
    suppliers +=(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers +=(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
    suppliers +=(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
    // Equivalent SQL code:
    // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    coffees ++= Seq(
      ("Colombian", 101, 7.99, 0, 0),
      ("French_Roast", 49, 8.99, 0, 0),
      ("Espresso", 150, 9.99, 0, 0),
      ("Colombian_Decaf", 101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
    // Equivalent SQL code:
    // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
  )

  val setupFuture = db.run(setup).map(f => {
    println("Coffees:")
    db.run(coffees.result).map(_.foreach {
      case (name, supID, price, sales, total) =>
        println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    })
  })

  Await.ready(setupFuture, 5 seconds)
}
