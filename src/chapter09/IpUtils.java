package chapter09;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/1
 */
public class IpUtils {
    public static int ipToInt(String ip) {
        String[] ipArray = ip.split("\\.");
        int num = 0;
        for (int i=0;i<ipArray.length;i++) {
            int valueOfSection = Integer.parseInt(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }

    public static String intToIp(int ip) {
        StringBuffer sb = new StringBuffer("");
        // 直接右移24位
        sb.append(String.valueOf((ip >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((ip & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((ip & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((ip & 0x000000FF)));
        return sb.toString();
    }

    public static long ipToLong(String strIp) {
        String[] s = strIp.split("\\.");
        long ip = (Long.parseLong(s[0]) << 24)
                + (Long.parseLong(s[1]) << 16) +
                (Long.parseLong(s[2]) << 8)
                + (Long.parseLong(s[3]));
        return ip;
    }

    public static String longToIp(long longIp) {
        //采用SB方便追加分隔符 "."
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(longIp>>24)).append(".").
                append(String.valueOf((longIp&0x00ffffff)>>16)).append(".").
                append(String.valueOf((longIp&0x0000ffff)>>8)).append(".").
                append(String.valueOf(longIp&0x000000ff));
        return sb.toString();
    }

    // 测试
    public static void main(String[] args) {
        int intIP;
        System.out.println(intIP = ipToInt("192.168.0.1"));
        System.out.println(ipToInt("192.168.0.254"));
        System.out.println(intToIp(intIP));
//        System.out.println(ipToInt("122.1.0.127"));
//        System.out.println(ipToInt("122.1.0.128"));
        System.out.println(ipToLong("192.168.0.127"));
        System.out.println(ipToLong("192.168.0.128"));
    }

}
