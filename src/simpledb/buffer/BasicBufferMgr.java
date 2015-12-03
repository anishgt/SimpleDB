package simpledb.buffer;

import simpledb.file.*;
import java.util.concurrent.ConcurrentHashMap;

//import java.util.Comparator;
//import java.util.PriorityQueue;
//java -classpath .\bin\ simpledb.server.Startup simpleDB
/**
 * Manages the pinning and unpinning of buffers to blocks.
 * 
 * @author Edward Sciore
 *
 */
public class BasicBufferMgr {
	private Buffer[] bufferpool;
	private int numAvailable;
	private ConcurrentHashMap<Block, Buffer> bufferPoolMap;
	// public PriorityQueue<Buffer> minLSN;

	/**
	 * Creates a buffer manager having the specified number of buffer slots.
	 * This constructor depends on both the {@link FileMgr} and
	 * {@link simpledb.log.LogMgr LogMgr} objects that it gets from the class
	 * {@link simpledb.server.SimpleDB}. Those objects are created during system
	 * initialization. Thus this constructor cannot be called until
	 * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or is called
	 * first.
	 * 
	 * @param numbuffs
	 *            the number of buffer slots to allocate
	 */
	BasicBufferMgr(int numbuffs) {
		bufferpool = new Buffer[numbuffs];
		numAvailable = numbuffs;
		bufferPoolMap = new ConcurrentHashMap<Block, Buffer>();
		// Comparator<Buffer> comparator = new bufferCompLSN();
		// minLSN = new PriorityQueue<Buffer>(numbuffs, comparator);
		for (int i = 0; i < numbuffs; i++)
			bufferpool[i] = new Buffer();
	}

	public Buffer[] getBufferpool() {
		return bufferpool;
	}

	public int getNumAvailable() {
		return numAvailable;
	}

	public ConcurrentHashMap<Block, Buffer> getBufferPoolMap() {
		return bufferPoolMap;
	}

	/**
	 * Flushes the dirty buffers modified by the specified transaction.
	 * 
	 * @param txnum
	 *            the transaction's id number
	 */
	synchronized void flushAll(int txnum) {
		for (Buffer buff : bufferpool)
			if (buff.isModifiedBy(txnum))
				buff.flush();
	}

	/**
	 * Pins a buffer to the specified block. If there is already a buffer
	 * assigned to that block then that buffer is used; otherwise, an unpinned
	 * buffer from the pool is chosen. Returns a null value if there are no
	 * available buffers.
	 * 
	 * @param blk
	 *            a reference to a disk block
	 * @return the pinned buffer
	 */
	synchronized Buffer pin(Block blk) {
		//System.out.println("Calling pin");
		Buffer buff = findExistingBuffer(blk);
		//System.out.println("Inside BasicBufferMgr pin");
		if (buff == null) {
			buff = chooseUnpinnedBuffer();
			if (buff == null)
				return null;
			buff.assignToBlock(blk);
		}
		if (!buff.isPinned()) {
			numAvailable--;
			// what about first line of code..
			// if there already is a mapping, what happens??
			// System.out.println("from BasicBufferMgr.pin(). added to
			// bufferPoolMap()");

		}
		bufferPoolMap.put(blk, buff);
		buff.pin();
		//iterateMap();
		return buff;
	}

	/**
	 * Allocates a new block in the specified file, and pins a buffer to it.
	 * Returns null (without allocating the block) if there are no available
	 * buffers.
	 * 
	 * @param filename
	 *            the name of the file
	 * @param fmtr
	 *            a pageformatter object, used to format the new block
	 * @return the pinned buffer
	 */
	synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
		//System.out.println("Calling pinNew");
		Buffer buff = chooseUnpinnedBuffer();
		if (buff == null)
			return null;
		buff.assignToNew(filename, fmtr);
		numAvailable--;
		bufferPoolMap.put(buff.block(), buff);
		buff.pin();
		return buff;
	}

	/**
	 * Unpins the specified buffer.
	 * 
	 * @param buff
	 *            the buffer to be unpinned
	 */
	synchronized void unpin(Buffer buff) {
		buff.unpin();
		if (!buff.isPinned()) {
			numAvailable++;
			bufferPoolMap.remove(buff.block());
			// System.out.println("from BasicBufferMgr.unpin(). removing from
			// bufferPoolMap()");
		}
	}

	/**
	 * Returns the number of available (i.e. unpinned) buffers.
	 * 
	 * @return the number of available buffers
	 */
	int available() {
		return numAvailable;
	}

	private Buffer findExistingBuffer(Block blk) {
		/*
		 * for (Buffer buff : bufferpool) { Block b = buff.block(); if (b !=
		 * null && b.equals(blk)) return buff; }
		 */
		return (bufferPoolMap.get(blk));
		// return null;
	}

	// private Buffer chooseUnpinnedBuffer() {
	// if (numAvailable > 0){
	// for (Buffer buff : bufferpool)
	// if (!buff.isPinned())
	// return buff;
	// } else {
	// //return minLSN.remove();
	// int minlsn = Integer.MAX_VALUE;
	// Buffer tempmin = null;
	// for (Buffer buff : bufferpool){
	// if ((buff.getLogSequenceNumber() < minlsn) &&
	// (buff.getLogSequenceNumber()>=0)){
	// minlsn= buff.getLogSequenceNumber();
	// tempmin = buff;
	// }
	// }
	// if (tempmin != null)
	// return tempmin;
	// }
	// return null;
	// }
	private Buffer chooseUnpinnedBuffer() {
		if (numAvailable > 0) {
			int minlsn = Integer.MAX_VALUE;
			Buffer tempmin = null;
			Buffer firstNegativeBuff = null;

			for (Buffer buff : bufferpool) {
				if ((!buff.isPinned()) && (buff.getLogSequenceNumber() < minlsn)
						&& (buff.getLogSequenceNumber() >= 0)) {
					minlsn = buff.getLogSequenceNumber();
					tempmin = buff;
				}
				if (!buff.isPinned() && (buff.getLogSequenceNumber() == -1) && (firstNegativeBuff == null))
					firstNegativeBuff = buff;
			}

			if (tempmin != null)
				return tempmin; // Unpinned buffer with Least positive LSN
			if (firstNegativeBuff != null)
				return firstNegativeBuff; // First Unpinned Buffer with negative
											// LSN
		}
		return null;
	}

	boolean containsMapping(Block blk) {
		return bufferPoolMap.containsKey(blk);
	}

	Buffer getMapping(Block blk) {
		return bufferPoolMap.get(blk);
	}

	public void getStatistics() {
		System.out.println("\n\nPrinting Statistics\n");
		int index = 0;
		for (Buffer buff : bufferpool) {
			System.out.println("Statistics for Buffer " + index);
			System.out.println("reads    :" + buff.getReadCount());
			System.out.println("writes   :" + buff.getWriteCount());
			System.out.println("flushes  :" + buff.getFlushCount());
			System.out.println("number of times pinned          :" + buff.getPinCount());
			System.out.println("number of times unpinned        :" + buff.getUnpinCount());
			System.out.println("number of times blocks assigned :" + buff.getBlockCount());
			System.out.println("newCount :" + buff.getNewCount());
			System.out.println("LSN :" + buff.getLogSequenceNumber());
			System.out.println("Last Modified by Txn: " + buff.getModifiedBy() + "\n");
			++index;
		}

	}
	public void iterateMap() {
		System.out.println("\n\nIterating over Buffer Pool Map : \n");
		for (ConcurrentHashMap.Entry<Block, Buffer> entry : bufferPoolMap.entrySet()) {
		    System.out.println("Block = " + entry.getKey() + ", Buffer = " + entry.getValue());
		}
	}
	public Buffer getBuffer(Block blk){
		return bufferPoolMap.get(blk);
	}
}
