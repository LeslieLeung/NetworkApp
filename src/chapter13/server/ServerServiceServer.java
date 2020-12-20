package chapter13.server;

import chapter13.rmi.ServerService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/20
 */
public class ServerServiceServer {
    public static void main(String[] args) {
        //为了避免和上一讲1099端口冲突，临时使用8008
        int port = 8008;
        try {
            //对于有多个网卡的机器，建议用下面的命令指绑定固定的ip，因为默认是绑定到0.0.0.0
            //System.setProperty("java.rmi.server.hostname",本机器的ip地址);
            //第一步，启动RMI注册器
            Registry registry = LocateRegistry.createRegistry(port);
            //第二步，实例化远程服务对象
            ServerService serverService = new ServerServiceImpl();
            //第三步，用助记符来注册及发布远程服务对象,助记符建议和远程服务接口命名相同，方便使用
            registry.rebind("ServerService", serverService);
            System.out.println("服务端发布了ServerService远程服务");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}

