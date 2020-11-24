package smartcity.ldgd.com.checkyfa64apitest;

import java.util.Arrays;

/**
 * Created by ldgd on 2019/11/19.
 * 功能：
 * 说明：
 */

public class check {
    private int i;

    public static void main(String[] args) {
    /*    int index = 15;

        List<String> imgs = new ArrayList<>();
        imgs.add("xzczxc");
        imgs.add("xzczxc2");
        imgs.add("xzczxc3");
        imgs.add("xzczxc4");

        System.out.println(imgs.toString());
        List<String> img = imgs.subList(0,2);
        System.out.println(img.toString());

        System.out.println(imgs == img);
        System.out.println(imgs == imgs);


        double electricalEnergy = 100-120;
        System.out.println(electricalEnergy);


        byte[] bytes = {-5, 49};

        // [-18, 4, 6, 0, 0, 0, 0, 0, 7, -5, 49, 4, -49, 0, 4, -49, 0, 0, -17]  1323
        System.out.println(bytesIntHL(new byte[]{bytes[0], bytes[1]}));
*/


        byte[] bytes = {-5, 49};

        // [-18, 4, 6, 0, 0, 0, 0, 0, 7, -5, 49, 4, -49, 0, 4, -49, 0, 0, -17]  1323

        int aa = bytesIntHL(new byte[]{bytes[0], bytes[1]});
        System.out.println(aa);
        System.out.println((char) aa);

       byte[] bb =  intBytesHL(-556,2);
        System.out.println(Arrays.toString(bb));
        System.out.println(bytesIntHL(bb));


    }


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
        } else if (bytes.length == 3) {
            int a = (bytes[0] & 0xff) << 16;
            int b = (bytes[1] & 0xff) << 8;
            int c = (bytes[2] & 0xff);
            result = a | b | c;
        } else if (bytes.length == 2) {
            int a = (bytes[0] & 0xff) << 8;
            int b = (bytes[1] & 0xff);
            result = a | b;
        }
        return result;
    }




    /**
     * 字节数组转int,适合转高位在前低位在后的byte[]
     *
     * @param bytes
     * @return
     */
    public static long byteArrayToLong(byte[] bytes) {
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


   /* *//**
     * 进入DataOutputStream查看它的write方法实现改造上述方法后：
     * int转byte[]，高位在前低位在后
     * @param value
     * @return
     *//*
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



}
