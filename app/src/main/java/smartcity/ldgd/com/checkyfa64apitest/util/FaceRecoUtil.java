package smartcity.ldgd.com.checkyfa64apitest.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ldgd on 2019/12/4.
 * 功能：
 * 说明：
 */

public class FaceRecoUtil {

    private List<String> imagePaths = new ArrayList<>();

    public List<String> getFilesAllName(String path) {

        if (imagePaths != null && imagePaths.size() > 0) {
            imagePaths.clear();
        }
        getFiles(path, imagePaths);

        Collections.sort(imagePaths);
        Collections.reverse(imagePaths);

        return imagePaths;
    }

    private void getFiles(String path, List<String> imagePaths) {
        //传入指定文件夹的路径
        File file = new File(path);
        File[] files = file.listFiles();

        if(files == null){
            return;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFiles(files[i].getPath(),imagePaths);
            } else {
                if (checkIsImageFile(files[i].getPath())) {
                    imagePaths.add(files[i].getPath());
                }
            }

        }
    }

    /**
     * 15  * 判断是否是照片
     * 16
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (fileEnd.equals("jpg") || fileEnd.equals("png")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    /**
     * 将文件按名字降序排列
     */
    class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            return file2.getName().compareTo(file1.getName());
        }
    }


}
