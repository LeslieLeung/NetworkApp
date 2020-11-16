package chapter11;

import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/16
 */
public class PacketSender {

    public static void sendTCPPacket(JpcapSender sender, int srcPort, int dstPort, String srcHost,
                                     String dstHost, String data, String srcMAC, String dstMAC,
                                     boolean syn, boolean ack, boolean rst, boolean fin) {
        try {
            // 构建tcp包
            TCPPacket tcp = new TCPPacket(srcPort, dstPort, 56, 78, false,
                    ack, false, rst, syn, fin, true, true, 200, 10);

            // 设置IPv4报头
            tcp.setIPv4Parameter(0, false, false, false, 0, false, false, false, 0, 1010101, 100, IPPacket.IPPROTO_TCP, InetAddress.getByName(srcHost),
                    InetAddress.getByName(dstHost));

            tcp.data = data.getBytes("UTF-8");

            EthernetPacket ethernetPacket = new EthernetPacket();
            ethernetPacket.frametype = EthernetPacket.ETHERTYPE_IP;
            tcp.datalink = ethernetPacket;

            ethernetPacket.src_mac = convertMacFormat(srcMAC);
            ethernetPacket.dst_mac = convertMacFormat(dstMAC);

            if (ethernetPacket.src_mac == null || ethernetPacket.dst_mac == null) {
                throw new Exception("MAC地址输入错误");
            }

            sender.sendPacket(tcp);
            System.out.println("发包成功！");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //重新抛出异常，调用者可以捕获处理
            throw new RuntimeException(e);

        }
    }

    public static byte[] convertMacFormat(String MAC) {
        String[] macs = new String[6];
        if (MAC.contains("-")) {
            macs = doSplit(MAC, "-");
        } else if (MAC.contains(":")) {
            macs = doSplit(MAC, ":");
        } else {
            return null;
        }
        byte[] result = new byte[6];
        for (int i = 0; i < macs.length; i++) {
            result[i] = (byte) Integer.parseInt(macs[i], 16);
        }
        return result;
    }

    private static String[] doSplit(String MAC, String splitter) {
        return MAC.split(splitter);
    }
}
