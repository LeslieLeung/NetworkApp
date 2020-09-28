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
    //定义字符输入流和输出流
    private PrintWriter pw;
    private BufferedReader br;

    FileDataClient(String ip, String port) throws IOException {
        //主动向服务器发起连接，实现TCP的三次握手过程
        //如果不成功，则抛出错误信息，其错误信息交由调用者处理
        dataSocket = new Socket(ip, Integer.parseInt(port));

        //得到网络输出字节流地址，并封装成网络输出字符流
        OutputStream socketOut = dataSocket.getOutputStream();
        pw = new PrintWriter( // 设置最后一个参数为true，表示自动flush数据
                new OutputStreamWriter(//设置utf-8编码
                        socketOut, "utf-8"), true);

        //得到网络输入字节流地址，并封装成网络输入字符流
        InputStream socketIn = dataSocket.getInputStream();
        br = new BufferedReader(
                new InputStreamReader(socketIn, "utf-8"));
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
