package finaltest3;

import finaltest3.rmi.StudentService;
import finaltest3.rmi.StudentServiceImpl;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StudentServer  {
    public static void main(String[] args) {
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
            StudentService studentService = new StudentServiceImpl();
            registry.rebind("StudentService", studentService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
