package chapter13.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 服务端远程对象接口，该接口为客户端提供服务
 * 由服务端实现以下远程方法
 */
public interface ServerService extends Remote {
    /**
     * 客户加入群组的远程方法
     *
     * @param client        格式为学号-姓名的字符串
     * @param clientService 用于将客户端的远程对象注入在线列表
     * @return 返回相关信息
     * @throws RemoteException
     */
    public String addClientToOnlineGroup(String client, ClientService clientService) throws RemoteException;


    /**
     * 客户退出群组的远程方法
     */
    public String removeClientFromOnlineGroup(String client, ClientService clientService) throws RemoteException;

    /**
     * 客户发送群聊信息的远程方法
     *
     * @param client 格式为学号-姓名的字符串
     * @param msg    要发送的信息
     * @throws RemoteException
     */
    public void sendPublicMsgToServer(String client, String msg) throws RemoteException;
}