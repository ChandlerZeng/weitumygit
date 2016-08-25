package com.libtop.weitu.activity.main.videoUpload;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
/**
 * <p>
 * Title: FormatUtil.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/4/27
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class FormatUtil {
    /**
     * 将缓存文件夹的数据转存到vedio文件下
     * @param recAudioFile
     */
    public static void videoRename(File recAudioFile) {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath()+ "/mahc/video/"+ "0" + "/";
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date()) + ".3gp";
        File out = new File(path);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(path, fileName);
        if (recAudioFile.exists())
            recAudioFile.renameTo(out);
    }

    /**
     * 用以计时操作的相关方法
     * @param num
     * @return
     */
    public static String format(int num){
        String s = num + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }
}
