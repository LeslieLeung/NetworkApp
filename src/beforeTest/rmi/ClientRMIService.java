package beforeTest.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/22
 */
public interface ClientRMIService extends Remote {
    public String reportResult(String msg) throws RemoteException;
}
