package chapter05.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupServer {
    private int port = 8008; //服务器监听端口
    private ServerSocket serverSocket; //定义服务器套接字
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public static Set<Socket> members = new CopyOnWriteArraySet<>();

    public GroupServer() throws IOException {
//        serverSocket = new ServerSocket(8008);
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

    private void sendToAllMembers(String msg, String hostAddress) throws IOException {
        PrintWriter pw;
        OutputStream out;

        for (Socket tempSocket : members) {
            out = tempSocket.getOutputStream();
            pw = new PrintWriter(
                    new OutputStreamWriter(out, "utf-8"), true);
            pw.println(hostAddress + " 发言：" + msg);
        }

    }

    class Handler implements Runnable {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            //本地服务器控制台显示客户端连接的用户信息
            System.out.println("New connection accepted： " + socket.getInetAddress());
            members.add(socket);
            try {
                BufferedReader br = getReader(socket);//定义字符串输入流
                PrintWriter pw = getWriter(socket);//定义字符串输出流

                //客户端正常连接成功，则发送服务器欢迎信息，然后等待客户发送信息
                pw.println("From 服务器：欢迎使用本服务！");

                String msg = null;
                //此处程序阻塞，每次从输入流中读入一行字符串
                while ((msg = br.readLine()) != null) {
                    //如果客户发送的消息为"bye"，就结束通信
                    if (msg.trim().equalsIgnoreCase("bye")) {
                        //向输出流中输出一行字符串,远程客户端可以读取该字符串
                        pw.println("From 服务器：服务器已断开连接，结束服务！");
                        System.out.println("客户端离开");
                        members.remove(socket);
                        break;//跳出循环读取
                    }
                    //向输出流中回传字符串,远程客户端可以读取该字符串
//                    pw.println("From 服务器：" + msg);
                    // 替换群组发送
                    sendToAllMembers(msg, socket.getInetAddress().getHostAddress());

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

    public void Service() throws IOException {
        ServerSocket TCPServer = new ServerSocket(8008);
        while (true) {
            Socket clientSocket = TCPServer.accept();
            executorService.execute(new Handler(clientSocket));
        }
    }

    public static void main(String[] args) throws IOException {
        new GroupServer().Service();
    }
}

