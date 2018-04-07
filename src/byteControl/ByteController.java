package byteControl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteController {
	public static byte[] intToByteArray(int integer) {
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
		
		buff.putInt(integer);
		buff.order(ByteOrder.BIG_ENDIAN);
		return buff.array();
	}
	
	public static int byteArrayToInt(byte[] bytes) {
		final int size = Integer.SIZE / 8;
		ByteBuffer buff = ByteBuffer.allocate(size);
		final byte[] newBytes = new byte[size];
		
		for(int i=0; i<size; i++) {
			if(i + bytes.length < size) {
				newBytes[i] = (byte)0x00;
			} else {
				newBytes[i] = bytes[i + bytes.length - size];
			}
		}
		
		buff = ByteBuffer.wrap(newBytes);
		buff.order(ByteOrder.BIG_ENDIAN);
		return buff.getInt();
	}
}
