import scala.slick.driver.H2Driver.simple._

object CaseClassMapping extends App {

  // the base query for the Users table
  val users = TableQuery[Users]

  val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")
  db.withSession { implicit session =>
    
    // create the schema
    users.ddl.create
    
    // insert two User instances
    users += User("John Doe")
    users += User("Fred Smith")
    
    // print the users (select * from USERS)
    println(users.list)
  }
  
}

case class User(name: String, id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  // Auto Increment the id primary key column
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  // The name can't be null
  def name = column[String]("NAME", O.NotNull)
  // the * projection (e.g. select * ...) auto-transforms the tupled
  // column values to / from a User
  def * = (name, id.?) <> (User.tupled, User.unapply)
}
