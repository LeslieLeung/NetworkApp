package chapter08;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/26
 */
public class HTTPSClient extends Client {
    private SSLSocket socket;
    private SSLSocketFactory factory;

    private PrintWriter pw;
    private BufferedReader br;

    public HTTPSClient(String ip, String port) throws IOException {
        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(ip, Integer.parseInt(port));

        OutputStream socketOut = socket.getOutputStream();
        pw = new PrintWriter(
                new OutputStreamWriter(
                        socketOut, "utf-8"
                ), true
        );

        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    @Override
    public void send(String msg) {
        //输出字符流，由Socket调用系统底层函数，经网卡发送字节流
        pw.println(msg);
    }

    @Override
    public String receive() {
        String msg = null;
        try {
            //从网络输入字符流中读信息，每次只能接受一行信息
            //如果不够一行（无行结束符），则该语句阻塞，
            // 直到条件满足，程序才往下运行
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void close() {
        try {
            if (socket != null) {
                //关闭socket连接及相关的输入输出流,实现四次握手断开
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
