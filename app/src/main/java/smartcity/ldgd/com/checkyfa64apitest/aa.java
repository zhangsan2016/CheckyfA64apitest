package smartcity.ldgd.com.checkyfa64apitest;

import java.io.UnsupportedEncodingException;

/**
 * Created by ldgd on 2019/11/11.
 * 功能：
 * 说明：
 */

public class aa {
    public static void main(String[] args) {
    /*    SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] devices = serialPortFinder.getAllDevices();
        System.out.println("String[] = " + Arrays.toString(devices));*/

       byte[] buffer = {53, 52, 54, 53, 52, 54, 115, 102, 100, 115, 100, 102, -76, -13, -54, -57, -76, -13, -73, -57};
        try {
            String str = new String(buffer,"utf-8");
            System.out.println(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
