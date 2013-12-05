import org.scalatest._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends FunSuite with BeforeAndAfter {

  val suppliers = TableQuery[Suppliers]
  val coffees = TableQuery[Coffees]
  
  implicit var session: Session = _

  def createSchema() = (suppliers.ddl ++ coffees.ddl).create
  
  def insertSupplier(): Int = suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
  
  before {
    session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()
  }
  
  test("Creating the Schema works") {
    createSchema()
    
    val tables = MTable.getTables().list()

    assert(tables.size == 2)
    assert(tables.count(_.name.name.equalsIgnoreCase("suppliers")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("coffees")) == 1)
  }

  test("Inserting a Supplier works") {
    createSchema()
    
    val insertCount = insertSupplier()
    assert(insertCount == 1)
  }
  
  test("Query Suppliers works") {
    createSchema()
    insertSupplier()
    val results = suppliers.list()
    assert(results.size == 1)
    assert(results.head._1 == 101)
  }
  
  after {
    session.close()
  }

}