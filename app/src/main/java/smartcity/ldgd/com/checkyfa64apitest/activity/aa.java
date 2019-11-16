package smartcity.ldgd.com.checkyfa64apitest.activity;

/**
 * Created by ldgd on 2019/11/14.
 * 功能：
 * 说明：
 */

public class aa   {


    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println(bitget(31,i));
        }


     //   System.out.println(bytesIntHL(new byte[]{4, -49}));

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

    /**
     * 二进制按位获取
     * @param num:要获取二进制值的数
     * @param index:倒数第一位为0，依次类推
     */
    public static int bitget(int num, int index)
    {
        return (num & (0x1 << index)) >> index;
    }






}
