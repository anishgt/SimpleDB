package simpledb.server;

import simpledb.remote.*;
import java.rmi.registry.*;
import java.util.Scanner;

public class Startup {
   public static void main(String args[]) throws Exception {
      // configure and initialize the database
      SimpleDB.init(args[0]);
      
      // create a registry specific for the server on the default port
      Registry reg = LocateRegistry.createRegistry(1099);
      
      // and post the server entry in it
      RemoteDriver d = new RemoteDriverImpl();
      reg.rebind("simpledb", d);
      
      
      System.out.println("database server ready");
      /*long time = 0;
      while (time <1000000000000000000L)
    	  ++time;*/
      
      Scanner sc=new Scanner(System.in);
      while (true){
	      System.out.println("Enter some strings when you're ready to getStatistics()");
	      sc.next();
	      SimpleDB.bufferMgr().getStatistics();
      }
   }
}
