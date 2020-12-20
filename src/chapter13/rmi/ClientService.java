package chapter13.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 客户端远程对象接口，该接口为服务端提供回调服务
 */
public interface ClientService extends Remote {
    /**
     * 由服务端主动推送消息到客户端，客户端刷新聊天信息的方法；
     * 该方法由客户端实现，服务端调用
     */
    public void showMsgToClient(String msg) throws RemoteException;
}
