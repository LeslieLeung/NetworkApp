package chapter12;

import rmi.RmiKitService;

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

    @Override
    public long ipToLong(String ip) throws RemoteException {
        String[] s = ip.split("\\.");
        long lIp = (Long.parseLong(s[0]) << 24)
                + (Long.parseLong(s[1]) << 16) +
                (Long.parseLong(s[2]) << 8)
                + (Long.parseLong(s[3]));
        return lIp;
    }

    @Override
    public String longToIp(long ipNum) throws RemoteException {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(ipNum >> 24)).append(".").
                append(String.valueOf((ipNum & 0x00ffffff) >> 16)).append(".").
                append(String.valueOf((ipNum & 0x0000ffff) >> 8)).append(".").
                append(String.valueOf(ipNum & 0x000000ff));
        return sb.toString();
    }

    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
        String[] macs = new String[6];
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

    private static String[] doSplit(String MAC, String splitter) {
        return MAC.split(splitter);
    }
}
