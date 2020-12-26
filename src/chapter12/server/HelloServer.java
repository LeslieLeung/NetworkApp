package chapter12.server;

import chapter12.rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class HelloServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            HelloService helloService = new HelloServerImpl("远程服务");
            registry.rebind("HelloService", helloService);

            System.out.println("发布了一个HelloService RMI服务");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
