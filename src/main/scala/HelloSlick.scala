import scala.slick.driver.H2Driver.simple._

// The main application
object HelloSlick extends App {

  // The query interface for the Suppliers table
  val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

  // the query interface for the Coffees table
  val coffees: TableQuery[Coffees] = TableQuery[Coffees]
  
  // Create a connection (called a "session") to an in-memory H2 database
  val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    (suppliers.ddl ++ coffees.ddl).create

  
    /* Create / Insert */
  
    // Insert some suppliers
    suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
    suppliers += ( 49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

    // Insert some coffees (using JDBC's batch insert feature)
    val coffeesInsertResult: Option[Int] = coffees ++= Seq (
      ("Colombian",         101, 7.99, 0, 0),
      ("French_Roast",       49, 8.99, 0, 0),
      ("Espresso",          150, 9.99, 0, 0),
      ("Colombian_Decaf",   101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
  
    val allSuppliers: List[(Int, String, String, String, String, String)] =
      suppliers.list

    // Print the number of rows inserted
    coffeesInsertResult foreach { numRows =>
      println(s"Inserted $numRows rows into the Coffees table")
    }

  
    /* Read / Query / Select */
  
    // Print the SQL for the Coffees query
    println("Generated SQL for base Coffees query:\n" + coffees.selectStatement)

    // Query the Coffees table using a foreach and print each row
    coffees foreach { case (name, supID, price, sales, total) =>
      println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    }


    /* Filtering / Where */

    // Construct a query where the price of Coffees is > 9.0
    val filterQuery: Query[Coffees, (String, Int, Double, Int, Int), Seq] =
      coffees.filter(_.price > 9.0)

    println("Generated SQL for filter query:\n" + filterQuery.selectStatement)

    // Execute the query
    println(filterQuery.list)
  

    /* Update */
  
    // Construct an update query with the sales column being the one to update
    val updateQuery: Query[Column[Int], Int, Seq] = coffees.map(_.sales)

    // Print the SQL for the Coffees update query
    println("Generated SQL for Coffees update:\n" + updateQuery.updateStatement)
  
    // Perform the update
    val numUpdatedRows = updateQuery.update(1)
  
    println(s"Updated $numUpdatedRows rows")


    /* Delete */

    // Construct a delete query that deletes coffees with a price less than 8.0
    val deleteQuery: Query[Coffees,(String, Int, Double, Int, Int), Seq] =
      coffees.filter(_.price < 8.0)

    // Print the SQL for the Coffees delete query
    println("Generated SQL for Coffees delete:\n" + deleteQuery.deleteStatement)

    // Perform the delete
    val numDeletedRows = deleteQuery.delete

    println(s"Deleted $numDeletedRows rows")
  
  
    /* Selecting Specific Columns */
  
    // Construct a new coffees query that just selects the name
    val justNameQuery: Query[Column[String], String, Seq] = coffees.map(_.name)
  
    println("Generated SQL for query returning just the name:\n" +
      justNameQuery.selectStatement)
  
    // Execute the query
    println(justNameQuery.list)
  
  
    /* Sorting / Order By */
  
    val sortByPriceQuery: Query[Coffees, (String, Int, Double, Int, Int), Seq] =
      coffees.sortBy(_.price)
  
    println("Generated SQL for query sorted by price:\n" +
      sortByPriceQuery.selectStatement)
  
    // Execute the query
    println(sortByPriceQuery.list)
  
  
    /* Query Composition */
  
    val composedQuery: Query[Column[String], String, Seq] =
      coffees.sortBy(_.name).take(3).filter(_.price > 9.0).map(_.name)
  
    println("Generated SQL for composed query:\n" +
      composedQuery.selectStatement)
  
    // Execute the composed query
    println(composedQuery.list)
  
  
    /* Joins */
  
    // Join the tables using the relationship defined in the Coffees table
    val joinQuery: Query[(Column[String], Column[String]), (String, String), Seq] = for {
      c <- coffees if c.price > 9.0
      s <- c.supplier
    } yield (c.name, s.name)

    println("Generated SQL for the join query:\n" + joinQuery.selectStatement)

    // Print the rows which contain the coffee name and the supplier name
    println(joinQuery.list)
    
    
    /* Computed Values */
    
    // Create a new computed column that calculates the max price
    val maxPriceColumn: Column[Option[Double]] = coffees.map(_.price).max
    
    println("Generated SQL for max price column:\n" + maxPriceColumn.selectStatement)
    
    // Execute the computed value query
    println(maxPriceColumn.run)
    
    
    /* Manual SQL / String Interpolation */

    // Required import for the sql interpolator
    import scala.slick.jdbc.StaticQuery.interpolation
  
    // A value to insert into the statement
    val state = "CA"
  
    // Construct a SQL statement manually with an interpolated value
    val plainQuery = sql"select SUP_NAME from SUPPLIERS where STATE = $state".as[String]
    
    println("Generated SQL for plain query:\n" + plainQuery.getStatement)
    
    // Execute the query
    println(plainQuery.list)
    
  }
}
