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
        for (int i = 0; i <= 26; i++) {
            imgs.add("img uri " + i);
        }


        for (int i = index; i < imgs.size(); i++) {
            System.out.println(imgs.get(i));
            imgs.clear();
        }

        for (int i = 0; i < imgs.size(); i++) {
            System.out.println(imgs.get(i));
        }
        System.out.println("完毕");

    }


}
