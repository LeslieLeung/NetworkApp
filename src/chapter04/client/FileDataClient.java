package chapter04.client;

import java.io.*;
import java.net.Socket;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/9/21
 */
public class FileDataClient {
    private Socket dataSocket; //定义套接字

    FileDataClient(String ip, String port) throws IOException {
        try {
            dataSocket = new Socket(ip, Integer.parseInt(port));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getFile(File saveFile) throws IOException {
        if (dataSocket != null) {
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            byte[] buf = new byte[1024];
            InputStream socketIn = dataSocket.getInputStream();
            OutputStream socketOut = dataSocket.getOutputStream();

            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);
            pw.println(saveFile.getName());

            int size = 0;
            while((size = socketIn.read(buf)) != -1) {
                fileOut.write(buf, 0, size);
            }
            fileOut.flush();
            fileOut.close();

            if (dataSocket != null) {
                dataSocket.close();
            } else {
                System.err.println("连接ftp数据服务器失败");
            }
        }
    }
}
