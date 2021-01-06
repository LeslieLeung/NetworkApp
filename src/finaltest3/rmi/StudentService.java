package finaltest3.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StudentService extends Remote {
    int getResult(String msgFromTeacher) throws RemoteException;
}
