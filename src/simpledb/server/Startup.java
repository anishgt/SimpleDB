package simpledb.server;

import simpledb.buffer.Buffer;
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
      
    //  Buffer[] buffpool = SimpleDB.bufferMgr().getBasicBufferMgr().getBufferpool();
 /*     buffpool[0].pin();
      buffpool[1].pin();
      buffpool[2].pin();
      buffpool[3].pin();
      buffpool[4].pin();
      buffpool[5].pin();
      buffpool[6].pin();
      buffpool[7].pin();
  */   
   /*   buffpool[0].setLogSequenceNumber(0);
      buffpool[1].setLogSequenceNumber(0);
      buffpool[3].setLogSequenceNumber(0);
      buffpool[2].setLogSequenceNumber(0);
      buffpool[4].setLogSequenceNumber(0);
      buffpool[5].setLogSequenceNumber(0);
      buffpool[6].setLogSequenceNumber(0);
      buffpool[7].setLogSequenceNumber(0);
  */
      Scanner sc=new Scanner(System.in);
      try{
      while (true){
	      System.out.println("Enter some strings when you're ready to getStatistics()");
	      sc.next();
	      SimpleDB.bufferMgr().getStatistics();
      }
      }finally{
      sc.close();
      }
   }
}
