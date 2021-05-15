package networkExercise.common;

import java.io.Serializable;

/**
 * Stores address details for a person.
 * 
 * @author Malcolm Corney
 * @version $Id: Exp $
 */
public class Person implements Comparable<Person>, Serializable {

   private static final long serialVersionUID = 332082608397623483L;

   private String name;

   private String street;

   private String suburb;

   private String phone;

   private String email;

   /**
    * No args constructor.
    */
   public Person() {
   }

   /**
    * Constructor to set values for the Person's details. 
    * @param name
    * @param street
    * @param suburb
    * @param phone
    * @param email
    */
   public Person(String name, String street, String suburb, String phone, String email) {
      this.name = name; 
      this.street= street; 
      this.suburb = suburb; 
      this.phone= phone; 
      this.email = email; 
   }

   /**
    * @return the email
    */
   public String getEmail() {
      return email;
   }

   /**
    * @param email the email to set
    */
   public void setEmail(String email) {
      this.email = email;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the phone
    */
   public String getPhone() {
      return phone;
   }

   /**
    * @param phone the phone to set
    */
   public void setPhone(String phone) {
      this.phone = phone;
   }

   /**
    * @return the street
    */
   public String getStreet() {
      return street;
   }

   /**
    * @param street the street to set
    */
   public void setStreet(String street) {
      this.street = street;
   }

   /**
    * @return the suburb
    */
   public String getSuburb() {
      return suburb;
   }

   /**
    * @param suburb the suburb to set
    */
   public void setSuburb(String suburb) {
      this.suburb = suburb;
   }

   /**
    * Compares this object with the specified object for order. Returns a
    * negative integer, zero, or a positive integer as this object is less than,
    * equal to, or greater than the specified object.
    * 
    * @param other The other Person object to compare against.
    * @return a negative integer, zero, or a positive integer as this object is
    *         less than, equal to, or greater than the specified object.
    * @throws ClassCastException if the specified object's type prevents it from
    *            being compared to this object.
    */
   public int compareTo(Person other) {
      return this.name.compareTo(other.name);
   }
   
   /**
    * @see Object#toString()
    */
   public String toString() {
      return name + " " + street + ", " + suburb + " " + phone + " " + email;
   }

}
