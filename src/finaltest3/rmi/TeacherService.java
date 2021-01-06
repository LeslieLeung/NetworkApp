package finaltest3.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TeacherService extends Remote {
    public String send(String msgToTeacher) throws RemoteException;
}
