package chapter12.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public interface RmiKitService extends Remote {
    //以下远程方法全部由学生端实现，教师端进行回调

    //远程方法一 将ipv4格式字符串转为长整型
    public long ipToLong(String ip) throws RemoteException;

    //远程方法二 将长整型转为ipv4字符串格式
    public String longToIp(long ipNum) throws RemoteException;

    //远程方法三 将":"或“-”格式的MAC地址转为Jpcap可用的字节数组
    public byte[] macStringToBytes(String macStr) throws RemoteException;

    //远程方法四 将Jpcap的byte[]格式的MAC地址转为可读的常见格式MAC字符串
    public String bytesToMACString(byte[] macBytes) throws RemoteException;

}
