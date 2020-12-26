package chapter12.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public interface RmiMsgService extends Remote {
    //声明远程方法一，用于学生发送信息给教师端，该方法由教师端实现，学生端调用
    public String send(String msg) throws RemoteException;

    //声明远程方法二 用于学生发送学号和姓名给教师端，该方法由教师端实现，学生端调用
    public String send(String yourNo, String yourName) throws RemoteException;

}
