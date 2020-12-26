package beforeTest.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/22
 */
public interface ServerRMIService extends Remote {
    public String getMessage(String url,String serviceName) throws RemoteException;
}
