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

    // 测试
    public static void main(String[] args) {
        int intIP;
        System.out.println(intIP = ipToInt("192.168.0.1"));
        System.out.println(ipToInt("192.168.0.254"));
        System.out.println(intToIp(intIP));
    }

}
