package chapter13.client;

import chapter13.rmi.ClientService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientServiceImpl extends UnicastRemoteObject implements ClientService {
    //获取客户端窗体变量（第3步创建该客户端窗体）
    private RmiClientFX rmiClientFX;

    public ClientServiceImpl(RmiClientFX rmiClientFX) throws RemoteException {
        this.rmiClientFX = rmiClientFX;
    }

    @Override
    public void showMsgToClient(String msg) throws RemoteException {
        //调用客户端窗体中定义的刷新窗体信息显示的方法
        rmiClientFX.appendMsg(msg);
    }
}
