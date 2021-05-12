package networkExercise.server;

import common.AddressBookDataSource;
import common.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;


/**
 * Class for retrieving data from the XML file holding the address list.
 */
public class JDBCAddressBookDataSource implements AddressBookDataSource {

   public static final String CREATE_TABLE =
           "CREATE TABLE IF NOT EXISTS address ("
                   + "idx INTEGER PRIMARY KEY /*!40101 AUTO_INCREMENT */ NOT NULL UNIQUE," // from https://stackoverflow.com/a/41028314
                   + "name VARCHAR(30),"
                   + "street VARCHAR(30),"
                   + "suburb VARCHAR(20),"
                   + "phone VARCHAR(10),"
                   + "email VARCHAR(30)" + ");";

   private static final String INSERT_PERSON = "INSERT INTO address (name, street, suburb, phone, email) VALUES (?, ?, ?, ?, ?);";

   private static final String GET_NAMES = "SELECT name FROM address";

   private static final String GET_PERSON = "SELECT * FROM address WHERE name=?";

   private static final String DELETE_PERSON = "DELETE FROM address WHERE name=?";

   private static final String COUNT_ROWS = "SELECT COUNT(*) FROM address";

   private Connection connection;

   private PreparedStatement addPerson;

   private PreparedStatement getNameList;

   private PreparedStatement getPerson;

   private PreparedStatement deletePerson;

   private PreparedStatement rowCount;

   public JDBCAddressBookDataSource() {
      connection = DBConnection.getInstance();
      try {
         Statement st = connection.createStatement();
         st.execute(CREATE_TABLE);
         /* BEGIN MISSING CODE */
         addPerson = connection.prepareStatement(INSERT_PERSON);
         getNameList = connection.prepareStatement(GET_NAMES);
         getPerson = connection.prepareStatement(GET_PERSON);
         deletePerson = connection.prepareStatement(DELETE_PERSON);
         rowCount = connection.prepareStatement(COUNT_ROWS);
         /* END MISSING CODE */
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * @see dataExercise.AddressBookDataSource#addPerson(dataExercise.Person)
    */
   public void addPerson(Person p) {
      try {
         /* BEGIN MISSING CODE */
         addPerson.setString(1, p.getName());
         addPerson.setString(2, p.getStreet());
         addPerson.setString(3, p.getSuburb());
         addPerson.setString(4, p.getPhone());
         addPerson.setString(5, p.getEmail());
         addPerson.execute();
         /* END MISSING CODE */
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * @see dataExercisenew.AddressBookDataSource#nameSet()
    */
   public Set<String> nameSet() {
      Set<String> names = new TreeSet<String>();
      ResultSet rs = null;

      /* BEGIN MISSING CODE */
      try {
         rs = getNameList.executeQuery();
         while (rs.next()) {
            names.add(rs.getString("name"));
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      /* END MISSING CODE */

      return names;
   }

   /**
    * @see dataExercise.AddressBookDataSource#getPerson(java.lang.String)
    */
   public Person getPerson(String name) {
      Person p = new Person();
      ResultSet rs = null;
      /* BEGIN MISSING CODE */
      try {
         getPerson.setString(1, name);
         rs = getPerson.executeQuery();
         rs.next();
         p.setName(rs.getString("name"));
         p.setStreet(rs.getString("street"));
         p.setSuburb(rs.getString("suburb"));
         p.setPhone(rs.getString("phone"));
         p.setEmail(rs.getString("email"));
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      /* END MISSING CODE */
      return p;
   }

   /**
    * @see dataExercise.AddressBookDataSource#getSize()
    */
   public int getSize() {
      ResultSet rs = null;
      int rows = 0;

      /* BEGIN MISSING CODE */
      try {
         rs = rowCount.executeQuery();
         rs.next();
         rows = rs.getInt(1);
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      /* END MISSING CODE */

      return rows;
   }

   /**
    * @see dataExercise.AddressBookDataSource#deletePerson(java.lang.String)
    */
   public void deletePerson(String name) {
      /* BEGIN MISSING CODE */
      try {
         deletePerson.setString(1, name);
         deletePerson.executeUpdate();
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      /* END MISSING CODE */
   }

   /**
    * @see dataExercise.AddressBookDataSource#close()
    */
   public void close() {
      /* BEGIN MISSING CODE */
      try {
         connection.close();
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      /* END MISSING CODE */
   }
}
