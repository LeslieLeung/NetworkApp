package chapter06.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private int remotePort;
    private InetAddress remoteIP;
    private DatagramSocket socket;//UDP套接字

    //用于接收数据的报文字节数组缓存最大容量，字节为单位
    private static final int MAX_PACKET_SIZE = 512;

    public UDPClient(String remoteIP, String remotePort) throws IOException {
        this.remoteIP = InetAddress.getByName(remoteIP);
        this.remotePort = Integer.parseInt(remotePort);
        //创建一个UDP套接字，系统随机选定一个未使用的UDP端口绑定
        socket = new DatagramSocket();
        //设置接收数据超时
        socket.setSoTimeout(30000);
    }

    //定义一个数据的发送方法
    public void send(String msg) {
        try {
            //将待发送的字符串转为字节数组
            byte[] outData = msg.getBytes("utf-8");
            //构建用于发送的数据报文，构造方法中传入远程通信方（服务器)的ip地址和端口
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, remoteIP, remotePort);
            //给UDPServer发送数据报
            socket.send(outPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定义数据接收方法
    public String receive() {
        String msg;
        //先准备一个空数据报文
        DatagramPacket inPacket = new DatagramPacket(
                new byte[MAX_PACKET_SIZE], MAX_PACKET_SIZE);
        try {
            //读取报文，阻塞语句，有数据就装包在inPacket报文中，以装完或装满为止。
            socket.receive(inPacket);
            //将接收到的字节数组转为对应的字符串
            msg = new String(inPacket.getData(),
                    0,inPacket.getLength(),"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            msg = null;
        }
        return msg;
    }
}
