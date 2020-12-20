package chapter13.server;

import chapter13.rmi.ClientService;
import chapter13.rmi.ServerService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/20
 */
public class ServerServiceImpl extends UnicastRemoteObject implements ServerService {
    //存储在线用户的map
    private static ConcurrentHashMap<String, ClientService> onlineGroup = new ConcurrentHashMap<>();

    protected ServerServiceImpl() throws RemoteException {
    }

    @Override
    //客户加入群组的远程方法，在client中包含自己的学号和姓名，格式为学号-姓名
    public String addClientToOnlineGroup(String client, ClientService clientService) throws RemoteException {
        if (client == null)
            return "From 服务器：学号姓名信息为空";
        if (client.split("-").length != 2)
            return "From 服务器：学号姓名格式不正确";

        boolean isLogin = false;
        //避免反复登录，关键是判断在线map中是否已经存在相同的clientService
        if (onlineGroup.containsKey(client)) {
            isLogin = true;
        }
        if (!isLogin) {
            onlineGroup.put(client.trim(), clientService);
            isLogin = true;
            // 群发新用户上线的信息
            sendPublicMsgToServer(client, "加入到群聊！");
            return "From 服务器：" + client.trim() + " 加入到群聊！";
        } else {
            return "From 服务器：不要反复登录";
        }
    }

    @Override
    //客户退出群组的远程方法
    public String removeClientFromOnlineGroup(String client, ClientService clientService) throws RemoteException {
        //删除的判断依据应该是clientService，因为client可能被用户修改了
        onlineGroup.remove(client);

        // 群发用户离线的信息
        sendPublicMsgToServer(client, "退出群聊！");
        return "From 服务器：" + client.trim() + " 退出群聊！";
    }

    @Override
    // //客户发送群聊信息的远程方法
    public void sendPublicMsgToServer(String client, String msg) throws RemoteException {
        if (msg != null) {
            //遍历在线map，获得所有客户端远程对象，进行消息群发
            //可能有客户端程序，退出时没有调用removeClientFromOnlineGroup，造成onlineGroup存在无效的用户值，群发消息时候，就会去连接这些不存在客户端的远程对象，造成socket超时错误，要考虑这种异常情况，否则可能导致所有客户端都无法使用
            for (String onlineUser : onlineGroup.keySet()) {
                //调用客户端远程对象的刷新信息方法，推送信息到客户端
                ClientService clientService = onlineGroup.get(onlineUser);
                if (clientService != null) {
                    clientService.showMsgToClient(msg);
                }
            }
        }
    }

}
