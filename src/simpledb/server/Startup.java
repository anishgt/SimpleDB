package simpledb.server;

import simpledb.buffer.Buffer;
import simpledb.file.Block;
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
      
      //Buffer[] buffpool = SimpleDB.bufferMgr().getBasicBufferMgr().getBufferpool();
      //buffpool[0].pin();
      //buffpool[1].pin();
 /*     buffpool[2].pin();
      buffpool[3].pin();
      buffpool[4].pin();
      buffpool[5].pin();
      buffpool[6].pin();
      buffpool[7].pin();
  */   
  /*    buffpool[3].setLogSequenceNumber(0);
      buffpool[2].setLogSequenceNumber(0);
      buffpool[4].setLogSequenceNumber(0);
      buffpool[5].setLogSequenceNumber(0);
      buffpool[6].setLogSequenceNumber(0);
      buffpool[7].setLogSequenceNumber(0);
  */
  /*    Scanner sc=new Scanner(System.in);
      while (true){
	      System.out.println("Enter some strings when you're ready to getStatistics()");
	      sc.next();
	      SimpleDB.bufferMgr().getStatistics();
	      
	      SimpleDB.bufferMgr().iterateMap();
      }
   */
 /*     
  	Block[] blocks = new Block[10];
  	for (int i=0; i< 10; i++){
  		blocks[i] = new Block(String.valueOf(i), i);
  	}
  	System.out.println("Pinning blocks: ");
  	for (int i=0; i<5; i++){
  		if (blocks[i]==null)
  			System.out.println("Block index "+i+" is null");
  		else
  			SimpleDB.bufferMgr().pin(blocks[i]);
  		System.out.println("Pinned block index: "+i);
  	}
  	for (int i=0; i<5; i++){
  		if (blocks[i]==null)
  			System.out.println("Block index "+i+" is null");
  		else
  			SimpleDB.bufferMgr().unpin(SimpleDB.bufferMgr().getMapping(blocks[i]));
  		System.out.println("UnPinned block index: "+i);
  	}
  	SimpleDB.bufferMgr().pin(blocks[0]);
  	SimpleDB.bufferMgr().pin(blocks[1]);
  	SimpleDB.bufferMgr().getStatistics();
 */
   }
}
