package networkExercise.common;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 * This version uses an AddressBookDataSource and its methods to retrieve data
 */
public class AddressBookData {

   DefaultListModel listModel;

   AddressBookDataSource addressData;

   /**
    * Constructor initializes the list model that holds names as Strings and
    * attempts to read any data saved from previous invocations of the
    * application.
    *
    */
   public AddressBookData(AddressBookDataSource dataSource) {
      listModel = new DefaultListModel();
      addressData = dataSource;

      // add the retrieved data to the list model
      for (String name : addressData.nameSet()) {
         listModel.addElement(name);
      }
   }

   /**
    * Adds a person to the address book.
    *
    * @param p A Person to add to the address book.
    */
   public void add(Person p) {

      // check to see if the person is already in the book
      // if not add to the address book and the list model
      if (!listModel.contains(p.getName())) {
         listModel.addElement(p.getName());
         addressData.addPerson(p);
      }
   }

   /**
    * Based on the name of the person in the address book, delete the person.
    *
    * @param key
    */
   public void remove(Object key) {

      // remove from both list and map
      listModel.removeElement(key);
      addressData.deletePerson((String) key);
   }

   /**
    * Saves the data in the address book using a persistence
    * mechanism.
    */
   public void persist() {
      addressData.close();
   }

   /**
    * Retrieves Person details from the model.
    *
    * @param key the name to retrieve.
    * @return the Person object related to the name.
    */
   public Person get(Object key) {
      return addressData.getPerson((String) key);
   }

   /**
    * Accessor for the list model.
    *
    * @return the listModel to display.
    */
   public ListModel getModel() {
      return listModel;
   }

   /**
    * @return the number of names in the Address Book.
    */
   public int getSize() {
      return addressData.getSize();
   }
}
