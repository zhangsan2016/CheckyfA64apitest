package smartcity.ldgd.com.checkyfa64apitest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ldgd on 2019/12/17.
 * 功能：
 * 说明：
 */

public class BytesUtil {


    /**
     * 字节数组转int,适合转高位在前低位在后的byte[]
     * @param bytes
     * @return
     */
    public static long byteArrayToLong(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        long result = 0;
        try {
            int len = dis.available();
            if (len == 1) {
                result = dis.readByte();
            } else if (len == 2) {
                result = dis.readShort();
            } else if (len == 4) {
                result = dis.readInt();
            } else if (len == 8) {
                result = dis.readLong();
            }
        } catch (IOException e) {
        } finally {
            try {
                dis.close();
                bais.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * int转byte[]，高位在前低位在后
     *
     * @param value
     * @return
     */
    public static byte[] varIntToByteArray(long value) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        Long l = new Long(value);
        try {
            if (l == l.byteValue()) {
                oos.writeByte(l.byteValue());
            } else if (l == l.shortValue()) {
                oos.writeShort(l.shortValue());
            } else if (l == l.intValue()) {
                oos.writeInt(l.intValue());
            } else if (l == l.longValue()) {
                oos.writeLong(l.longValue());
            } else if (l == l.floatValue()) {
                oos.writeFloat(l.floatValue());
            } else if (l == l.doubleValue()) {
                oos.writeDouble(l.doubleValue());
            }
        } catch(IOException e) {
        } finally {
            try {
                baos.close();
                oos.close();
            } catch (IOException e) {
            }
        }
        return baos.toByteArray();
    }


    /* *//**
     * 进入DataOutputStream查看它的write方法实现改造上述方法后：
     * int转byte[]，高位在前低位在后
     * @param value
     * @return
     *//*
     *
     *    public static long byteArrayToLong(byte[] bytes) {
        long result = 0;
        int len = bytes.length;
        if (len == 1) {
            byte ch = (byte) (bytes[0] & 0xff);
            result = ch;
        } else if (len == 2) {
            int ch1 = bytes[0] & 0xff;
            int ch2 = bytes[1] & 0xff;
            result = (short) ((ch1 << 8) | (ch2 << 0));
        } else if (len == 4) {
            int ch1 = bytes[0] & 0xff;
            int ch2 = bytes[1] & 0xff;
            int ch3 = bytes[2] & 0xff;
            int ch4 = bytes[3] & 0xff;
            result = (int) ((ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0));
        } else if (len == 8) {
            long ch1 = bytes[0] & 0xff;
            long ch2 = bytes[1] & 0xff;
            long ch3 = bytes[2] & 0xff;
            long ch4 = bytes[3] & 0xff;
            long ch5 = bytes[4] & 0xff;
            long ch6 = bytes[5] & 0xff;
            long ch7 = bytes[6] & 0xff;
            long ch8 = bytes[7] & 0xff;
            result = (ch1 << 56) | (ch2 << 48) | (ch3 << 40) | (ch4 << 32) | (ch5 << 24) | (ch6 << 16) | (ch7 << 8) | (ch8 << 0);
        }
        return result;
    }
    public static byte[] varIntToByteArray(long value) {
        Long l = new Long(value);
        byte[] valueBytes = null;
        if (l == l.byteValue()) {
            valueBytes = toBytes(value, 1);
        } else if (l == l.shortValue()) {
            valueBytes = toBytes(value, 2);
        } else if (l == l.intValue()) {
            valueBytes = toBytes(value, 4);
        } else if (l == l.longValue()) {
            valueBytes = toBytes(value, 8);
        }
        return valueBytes;
    }

    private static byte[] toBytes(long value, int len) {
        byte[] valueBytes = new byte[len];
        for (int i = 0;i < len;i++) {
            valueBytes[i] = (byte) (value >>> 8 * (len - i - 1));
        }
        return valueBytes;
    }*/

    /**
     * 字节数组转int,适合转高位在前低位在后的byte[]
     *
     * @param bytes
     * @return
     */



}
