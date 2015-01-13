package com.tutorialspoint;

import java.util.*;

public class CollectionsDemo {
	
   public static void main(String args[]) {
	   Random rnd = new Random(0);
	   
	   for (int i = 0; i < 3; i++) {
      // create array list object       
      List arrlist = new ArrayList();
      
      
      
      // populate the list
      arrlist.add("A");
      arrlist.add("B");
      arrlist.add("C");  
      arrlist.add("D");
      arrlist.add("E");
      
      System.out.println("Initial collection: "+arrlist);
      
      // shuffle the list
      
    	  Collections.shuffle(arrlist, rnd);
      
      System.out.println("Final collection after shuffle: "+arrlist);
	   }
   }    
} 