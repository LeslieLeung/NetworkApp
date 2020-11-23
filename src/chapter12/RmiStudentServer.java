package chapter12;

import rmi.RmiKitService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class RmiStudentServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            RmiKitService rmiKitService = new RmiKitServiceImpl();
            registry.rebind("RmiKitService", rmiKitService);

            System.out.println("发布了一个RmiKitService RMI服务");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
