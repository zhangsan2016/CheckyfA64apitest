package smartcity.ldgd.com.checkyfa64apitest.util;

/**
 * Created by ldgd on 2019/11/15.
 * 功能：
 * 说明：
 */

public class MyByteUtil {



    //高位在前，低位在后，区分byte长度
    public static byte[] intBytesHL(int num, int length) {

        byte[] result = new byte[length];
        if (length == 2) {
            result[0] = (byte) ((num >>> 8) & 0xff);
            result[1] = (byte) ((num >>> 0) & 0xff);
        } else if (length == 4) {
            result[0] = (byte) ((num >>> 24) & 0xff);//说明一
            result[1] = (byte) ((num >>> 16) & 0xff);
            result[2] = (byte) ((num >>> 8) & 0xff);
            result[3] = (byte) ((num >>> 0) & 0xff);
        }

        return result;
    }

    //高位在前，低位在后，区分byte长度
    public static int bytesIntHL(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;//说明二
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        }else if(bytes.length == 3){
            int a = (bytes[0] & 0xff) << 24;
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            result = a | b | c ;
        } else if(bytes.length == 2){
            int a = (bytes[0] & 0xff) <<  8;
            int b = (bytes[1] & 0xff);
            result = a | b;
        }
        return result;
    }
}
