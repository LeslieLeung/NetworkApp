package chapter02;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private int port = 8008; //服务器监听端口
    private ServerSocket serverSocket; //定义服务器套接字

    public TCPServer() throws IOException {
        serverSocket = new ServerSocket(port);
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

    //单客户版本，即每一次只能与一个客户建立通信连接
    public void Service() {
        while (true) {
            Socket socket = null;
            try {
                //此处程序阻塞，监听并等待客户发起连接，有连接请求就生成一个套接字。
                socket = serverSocket.accept();
                //本地服务器控制台显示客户端连接的用户信息
                System.out.println("New connection accepted： " + socket.getInetAddress());
                BufferedReader br = getReader(socket);//定义字符串输入流
                PrintWriter pw = getWriter(socket);//定义字符串输出流
                //客户端正常连接成功，则发送服务器的欢迎信息，然后等待客户发送信息
                pw.println("From 服务器：欢迎使用本服务！");

                String msg = null;
                //此处程序阻塞，每次从输入流中读入一行字符串
                while ((msg = br.readLine()) != null) {
                    //如果客户发送的消息为"bye"，就结束通信
                    if (msg.equals("bye")) {
                        //向输出流中输出一行字符串,远程客户端可以读取该字符串
                        pw.println("From服务器：服务器断开连接，结束服务！");
                        System.out.println("客户端离开");
                        break; //结束循环
                    }
                    //向输出流中输出一行字符串,远程客户端可以读取该字符串
                    // 正则表达式实现“人工智能”（扩展练习）
                    msg = msg.replaceAll("[吗?？]", "") + "!";
                    pw.println("From服务器：" + msg);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close(); //关闭socket连接及相关的输入输出流
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new TCPServer().Service();
    }
}

