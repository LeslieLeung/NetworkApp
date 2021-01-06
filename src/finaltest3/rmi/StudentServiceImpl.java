package finaltest3.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class StudentServiceImpl extends UnicastRemoteObject implements StudentService{

    public StudentServiceImpl() throws RemoteException {
    }

    @Override
    public int getResult(String msgFromTeacher) throws RemoteException {
        int result = 0;
        if (msgFromTeacher.contains("#")) {
            //max
            String msg = msgFromTeacher.substring(2);
            String[] numbers = msg.split("-");
            for (String number:numbers
                 ) {
                int number_ = Integer.parseInt(number);
                if (number_ > result) {
                    result = number_;
                }
            }
        } else if (msgFromTeacher.contains("*")) {
            //min
            String msg = msgFromTeacher.substring(2);
            String[] numbers = msg.split("-");
            for (String number:numbers
            ) {
                int number_ = Integer.parseInt(number);
                if (number_ < result) {
                    result = number_;
                }
            }
        }
        return result;
    }
}
