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


    }


}
