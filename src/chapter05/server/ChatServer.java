package chapter05.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer {
    private int port = 8008; //服务器监听端口
    private ServerSocket serverSocket; //定义服务器套接字
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    //    public static Set<Socket> members = new CopyOnWriteArraySet<>();
    public static Map<Socket, String> members = new ConcurrentHashMap<>();
    public static Map<String, Socket> membersR = new ConcurrentHashMap<>();

    public ChatServer() throws IOException {
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

        for (Socket tempSocket : members.keySet()) {
            out = tempSocket.getOutputStream();
            pw = new PrintWriter(
                    new OutputStreamWriter(out, "utf-8"), true);
            pw.println(hostAddress + " 发言：" + msg);
        }

//        Socket tempSocket;
//
//        Iterator<Socket> iterator = members.iterator();
//        while (iterator.hasNext()) {//遍历在线客户Set集合
//            tempSocket = iterator.next(); //取出一个客户的socket
////            String hostName = tempSocket.getInetAddress().getHostName();
////            String hostAddress = tempSocket.getInetAddress().getHostAddress();
//            out = tempSocket.getOutputStream();
//            pw = new PrintWriter(
//                    new OutputStreamWriter(out, "utf-8"), true);
//            pw.println(tempSocket.getInetAddress() + " 发言：" + msg );
//        }

    }

    private void notifyAllMembers(String msg) throws IOException {
        PrintWriter pw;
        OutputStream out;

        for (Socket tempSocket : members.keySet()) {
            out = tempSocket.getOutputStream();
            pw = new PrintWriter(
                    new OutputStreamWriter(out, "utf-8"), true);
            pw.println("服务器广播：" + msg);
        }
    }

    private void sendToMember(String msg, Socket memberSocket) throws IOException {
        PrintWriter pw;
        OutputStream out;

        out = memberSocket.getOutputStream();
        pw = new PrintWriter(
                new OutputStreamWriter(out, "utf-8"), true
        );
        pw.println(msg);
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
            try {
                BufferedReader br = getReader(socket);//定义字符串输入流
                PrintWriter pw = getWriter(socket);//定义字符串输出流

                //客户端正常连接成功，则发送服务器欢迎信息，然后等待客户发送信息
                pw.println("From 服务器：欢迎使用本服务！");
                // 检验是否已经登录
                if (!members.containsKey(socket)) {
                    pw.println("From 服务器：请先输入学号-姓名登录");
                }
//                pw.println("From 服务器：请先输入学号-姓名登录");

//                // 正则表达式 匹配学号-姓名
//                Pattern pattern = Pattern.compile("([0-9]{11})-(.*)");

                String msg = null;
                //此处程序阻塞，每次从输入流中读入一行字符串
                while ((msg = br.readLine()) != null) {
                    // 处理登录
                    if (msg.trim().matches("([0-9]{11})-(.*)")) {
                        members.put(socket, msg.trim());
                        membersR.put(msg.trim(), socket);
                        pw.println("From 服务器：登录成功，你的信息为" + msg.trim());
                        notifyAllMembers(msg.trim() + "上线");
                    } else if (msg.trim().contains("@")) {
                        System.out.println("私聊模式");
                        // 一对一或一对多私聊
                        StringBuilder privateMsg = new StringBuilder();
                        privateMsg.append(members.get(socket)+"发送给你的私聊信息：");

                        // 将信息分段处理
                        // 预期信息格式为"[@学号（11位数字）-姓名]*n '[msg]'"
                        // 分割字符串
                        String[] all = msg.trim().split(" ");
                        System.out.println(Arrays.toString(all));
                        Set recipients = new CopyOnWriteArraySet<String>();

                        // 匹配@信息
                        Pattern recepient = Pattern.compile("@(.*)?");
                        for (String str : all) {
                            Matcher matcher = recepient.matcher(str);
                            if (matcher.find()) {
                                recipients.add(matcher.group(1));
                            }
                        }

                        // 匹配发送信息
                        Pattern msgpat = Pattern.compile("'(.*)'");
                        for (String str:all) {
                            Matcher matcher = msgpat.matcher(str);
                            if (matcher.find()) {
                                privateMsg.append(matcher.group(1));
                            }
                        }

                        // 确定要发送的socket集合
                        Set recepientSockets = new CopyOnWriteArraySet<Socket>();
                        recipients.forEach((receiver)-> {
                            Socket tempSocket = membersR.get(receiver);
                            recepientSockets.add(tempSocket);
                        });
                        // 发送信息
                        recepientSockets.forEach((socket) -> {
                            try {
                                sendToMember(privateMsg.toString(), (Socket) socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else if (msg.trim().equalsIgnoreCase("who")) {
                        // 显示所有在线用户
                        StringBuilder str = new StringBuilder();
                        str.append("目前在线用户：\n");
                        for (String member : members.values()) {
                            str.append("\t" + member + "\n");
                        }
                        pw.println(str);
                    } else if (msg.trim().equalsIgnoreCase("bye")) {
                        //向输出流中输出一行字符串,远程客户端可以读取该字符串
                        pw.println("From 服务器：服务器已断开连接，结束服务！");
                        System.out.println("客户端离开");
                        // 从members和membersR中删除登录信息
                        String client = members.get(socket);
                        members.remove(socket);
                        membersR.remove(client);
                        break;//跳出循环读取
                    } else if (!members.containsKey(socket)) {
                        // 如果全部都无匹配，且未登录，则肯定为输入格式出错（指不满足学号-姓名的格式），报错
                        pw.println("From 服务器：输入格式有误，请检查后重新发送");
                    }
                    else {
                        // 群发
                        sendToAllMembers(msg, members.get(socket));
                    }


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
        new ChatServer().Service();
    }
}

