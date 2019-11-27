package smartcity.ldgd.com.checkyfa64apitest;

import smartcity.ldgd.com.checkyfa64apitest.util.MyByteUtil;

/**
 * Created by ldgd on 2019/11/19.
 * 功能：
 * 说明：
 */

public class aa {

    public static void main(String[] args) {

        System.out.println(MyByteUtil.bytesIntHL(new byte[]{5,-8}));

     /*   int lengIndex = 7;


        byte[] buff = {-18, 4, 5, 0, 0, 0, 0, 0, 14, 48, 24, 48, 24, 4, -49, 0, 4, -46, 0, 123, 0, 123, 31, 127, -115, -17};
      *//*  CopyOfcheckCRC.checkTheCrc(
                Arrays.copyOfRange(data, 5, 47),
                Arrays.copyOfRange(data, 47, 49));*//*

        // [48, 24, 48, 24, 4, -49, 0, 4, -46, 0, 123, 0, 123, 31]
        System.out.println(Arrays.toString(copyOfRange(buff, lengIndex, lengIndex + 2)));
        lengIndex = lengIndex + 2;

        byte[] dataCrc = copyOfRange(buff, 9, 23);

        int dataSize = MyByteUtil.bytesIntHL(copyOfRange(buff, 7, 9));
        if (dataSize > 0) {
            byte[] data = Arrays.copyOfRange(buff, 9, 9 + dataSize);
            byte[] crc = Arrays.copyOfRange(buff, 9 + dataSize, 9 + dataSize + 2);
            System.out.println("data = " + Arrays.toString(data));

            System.out.println("crc = " + Arrays.toString(Arrays.copyOfRange(buff, 9 + dataSize, 9 + dataSize + 2)));

            CheckCRC.checkTheCrc(data,crc);
            System.out.println("xxxx = " +  CheckCRC.checkTheCrc(crc,data));

        }*/


    }


}
