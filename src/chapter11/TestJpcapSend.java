package chapter11;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.TCPPacket;

import java.io.IOException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/16
 */
public class TestJpcapSend {
    public static void main(String[] args) throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender sender = JpcapSender.openDevice(devices[4]);

    }

}
