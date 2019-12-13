package smartcity.ldgd.com.checkyfa64apitest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldgd on 2019/11/19.
 * 功能：
 * 说明：
 */

public class aa {
    private int i;

    public static void main(String[] args) {
        int index = 15;

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

        // [-18, 4, 6, 0, 0, 0, 0, 0, 7, -5, 49, 4, -49, 0, 4, -49, 0, 0, -17]
        System.out.println(bytesIntHL(new byte[]{bytes[0], bytes[1]}));



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
            int a = (bytes[0] & 0xff) << 16;
            int b = (bytes[1] & 0xff) << 8;
            int c = (bytes[2] & 0xff);
            result = a | b | c ;
        } else if(bytes.length == 2){
            int a = (bytes[0] & 0xff) <<  8;
            int b = (bytes[1] & 0xff);
            result = a | b;
        }
        return result;
    }


}
