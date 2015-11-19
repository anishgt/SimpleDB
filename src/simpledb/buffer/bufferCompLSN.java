package simpledb.buffer;

import java.util.Comparator;

public class bufferCompLSN implements Comparator<Buffer> {

	@Override
	public int compare(Buffer o1, Buffer o2) {
		// TODO Auto-generated method stub
		if (o1.getLogSequenceNumber() < o2.getLogSequenceNumber())
			return 1;
		if (o1.getLogSequenceNumber() > o2.getLogSequenceNumber())
			return -1;
		return 0;
	}

}
