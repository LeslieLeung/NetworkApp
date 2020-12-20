package chapter06.server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/8
 */
public class UDPServer {
    private int port = 8008;
    private DatagramSocket socket;

    private static final int MAX_PACKET_SIZE = 512;

    public UDPServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("服务器启动监听在 " + port + " 端口");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        //获得输出流缓冲区的地址
        OutputStream socketOut = socket.getOutputStream();
        //网络流写出需要使用flush，这里在PrintWriter构造方法中直接设置为自动flush
        return new PrintWriter(
                new OutputStreamWriter(socketOut, "utf-8"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        //获得输入流缓冲区的地址
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(
                new InputStreamReader(socketIn, "utf-8"));
    }

    //定义一个数据的发送方法
    private void send(String msg, InetAddress ip, int port) {
        try {
            //将待发送的字符串转为字节数组
            byte[] outData = msg.getBytes("utf-8");
            //构建用于发送的数据报文，构造方法中传入远程通信方的ip地址和端口
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, ip, port);
            //给UDPServer发送数据报
            socket.send(outPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String doAlter(String msg) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("***");
        String date = new Date().toString();
        stringBuilder.append(date);
        stringBuilder.append("&");
        stringBuilder.append(msg);
        return stringBuilder.toString();
    }

    public void service() {
        while (true) {
            String msg;
            //先准备一个空数据报文
            DatagramPacket inPacket = new DatagramPacket(
                    new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
            // 客户端的ip和port
            InetAddress ip;
            int port;
            try {
                //读取报文，阻塞语句，有数据就装包在inPacket报文中，以装完或装满为止。
                socket.receive(inPacket);
                // 通过getAddress和getPort方法获取客户端的ip地址和端口
                ip = inPacket.getAddress();
                port = inPacket.getPort();
                //将接收到的字节数组转为对应的字符串
                msg = new String(inPacket.getData(),
                        0,inPacket.getLength(),"utf-8");
            } catch (IOException e) {
                e.printStackTrace();
                msg = null;
                ip = null;
                port = 0;
            }
//            // 判分用
//            send(doAlter(msg), ip, port);
            assert msg != null;
            send(msg, ip, port);
        }
    }

    public static void main(String[] args) throws IOException {
        new UDPServer().service();
    }
}
