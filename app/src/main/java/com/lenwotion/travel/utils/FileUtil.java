package com.lenwotion.travel.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 文件操作工具类
 * Created by John on 2017/2/15.
 */

public class FileUtil {

    /**
     * APP 文件夹名称
     */
    public static String DIR_TYPE = "/" + "LenwotionApp" + "/";
    /**
     * APP 文件夹路径
     */
    public static String DIR = Environment.getExternalStorageDirectory() + DIR_TYPE;

    /**
     * 创建APP文件夹
     */
    public static void createAppFloder() {
        // 创建文件夹
        File destDir = new File(DIR);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    /**
     * 创建文件
     */
    public static File makeFilePath(String fileName) {
        if (DIR.isEmpty()) {
            createAppFloder();
        }
        File file = null;
        try {
            file = new File(DIR, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(DIR + filePath);
        return file.isFile() && file.exists() && file.delete();
    }

    /**
     * 删除目录下所有文件
     * path = 目录路径
     */
    public static void deleteAllFiles(String path) {
        deleteAllFiles(new File(path));
    }

    /**
     * 删除目录下所有文件
     */
    public static void deleteAllFiles(File file) {
        if (file == null || !file.exists() || !file.isDirectory())
            return;
        for (File files : file.listFiles()) {
            if (files.isFile()) {
                files.delete(); //删除所有文件
            } else if (files.isDirectory()) {
                deleteAllFiles(files); //递规的方式删除文件夹
            }
        }
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 将字符串写入到文本文件中
     */
    public static void writeTextToFile(String string, String fileName) {
        // 每次写入时，都换行写
        String strContent = string + "\r\n";
        try {
            File file = makeFilePath(fileName);
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取用户保存的内容
     */
    public static String readTextFromFile(String fileName) {
        try {
            File file = new File(DIR, fileName);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[4096];
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while (inputStream.read(bytes) != -1) {
                arrayOutputStream.write(bytes, 0, bytes.length);
            }
            inputStream.close();
            arrayOutputStream.close();
            return new String(arrayOutputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 将文件转换成bytes
     * byte[] bytes = FileUtil.getBytesFromFile(new File("D:/test.png"));
     */
    public static byte[] getBytesFromFile(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
            byte[] bytes = new byte[4096];
            int i;
            while ((i = fileInputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, i);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据byte数组，生成文件
     * FileUtil.getFileFromBytes(bytes, "D:", "new test.png");
     */
    public static File getFileFromBytes(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bufferedOutputStream = null;
        FileOutputStream fileOutputStream = null;
        File file = null;
        try {
            File fileDir = new File(filePath);
            // 判断文件目录是否存在
            if (!fileDir.exists() && fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
            // windows下是这样的路径组合
            // file = new File(filePath + "\\" + fileName);
            // 安卓下是这样
            file = new File(filePath + "/" + fileName);
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len;
        byte[] buf;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
