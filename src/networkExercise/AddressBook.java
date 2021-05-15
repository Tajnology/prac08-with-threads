package networkExercise;
import networkExercise.common.*;
import javax.swing.*;

/**
 * Invokes the Address Book application.
 * @author Malcolm Corney
 * @version $Id:  Exp $
 */
public class AddressBook {
   
   /**
    * Create the GUI.  For thread safety,
    * this method should be invoked from the
    * event-dispatching thread.
    */
   private static void createAndShowGUI() {
      new AddressBookUI(
              new AddressBookData(
                      new NetworkDataSource()
              )
      );
   }

   /**
    * @param args
    */
   public static void main(String[] args) {
       //Schedule a job for the event-dispatching thread:
       //creating and showing this application's GUI.
       SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               createAndShowGUI();
           }
       });
   }
}
