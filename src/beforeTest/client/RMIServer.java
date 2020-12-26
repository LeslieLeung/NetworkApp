package beforeTest.client;

import beforeTest.rmi.ClientRMIService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/22
 */
public class RMIServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            ClientRMIService clientRMIService = new ClientRMIServiceImpl();
            registry.rebind("ClientRMIService", clientRMIService);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
