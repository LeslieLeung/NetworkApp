package chapter12;

import chapter12.rmi.RmiKitService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class RmiKitServiceImpl extends UnicastRemoteObject implements RmiKitService {
    public RmiKitServiceImpl() throws RemoteException {

    }

    /**
     * 将字符串类型的ip转换成长整型（long）
     * 方便进行遍历（+1即可）
     * @param ip String
     * @return long
     * @throws RemoteException
     */
    @Override
    public long ipToLong(String ip) throws RemoteException {
        String[] s = ip.split("\\.");
        long lIp = (Long.parseLong(s[0]) << 24)
                + (Long.parseLong(s[1]) << 16) +
                (Long.parseLong(s[2]) << 8)
                + (Long.parseLong(s[3]));
        return lIp;
    }

    /**
     * 将long类型的ip转换成字符串
     * @param ipNum long
     * @return String
     * @throws RemoteException
     */
    @Override
    public String longToIp(long ipNum) throws RemoteException {
        // 由于作为服务端，因此使用线程安全的StringBuffer而不是StringBuilder，两者区别和使用详见https://blog.csdn.net/itchuxuezhe_yang/article/details/89966303
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(ipNum >> 24)).append(".").
                append(String.valueOf((ipNum & 0x00ffffff) >> 16)).append(".").
                append(String.valueOf((ipNum & 0x0000ffff) >> 8)).append(".").
                append(String.valueOf(ipNum & 0x000000ff));
        return sb.toString();
    }

    /**
     * 将MAC地址转换成Bytes
     * @param macStr String
     * @return byte[]
     * @throws RemoteException
     */
    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
        String[] macs = new String[6];
        // 可接受-和:分隔的MAC字符串
        if (macStr.contains("-")) {
            macs = doSplit(macStr, "-");
        } else if (macStr.contains(":")) {
            macs = doSplit(macStr, ":");
        } else {
            return null;
        }
        byte[] result = new byte[6];
        for (int i = 0; i < macs.length; i++) {
            result[i] = (byte) Integer.parseInt(macs[i], 16);
        }
        return result;
    }

    /**
     * 将byte转换成MAC字符串
     * @param macBytes byte[]
     * @return String
     * @throws RemoteException
     */
    @Override
    public String bytesToMACString(byte[] macBytes) throws RemoteException {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < macBytes.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            int temp = macBytes[i] & 0xff;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                sb.append("0" + str);
            } else {
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 切分MAC字符串工厂方法（伪）
     * @param MAC String MAC地址
     * @param splitter String 分隔符
     * @return
     */
    private static String[] doSplit(String MAC, String splitter) {
        return MAC.split(splitter);
    }
}
