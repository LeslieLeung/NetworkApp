package beforeTest.client;

import beforeTest.rmi.ServerRMIService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/22
 */
public class Test {
    private static ServerRMIService serverRMIService;
    public static void main(String[] args) throws RemoteException {
        try {
            RMIClientSocketFactory clientSocketFactory;
            Registry registry = LocateRegistry.getRegistry("202.116.195.81", 1099);
            serverRMIService = (ServerRMIService) registry.lookup("RMIService2");
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverRMIService.getMessage("192.168.0.111:1099", "ClientRMIService");
    }
}
