package chapter10;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;


/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/9
 */
public class TestJpcapCapture {
    public static void main(String[] args) throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            //print out its GUID information and description
            System.out.println(i+": "+devices[i].name + devices[i].description);
            //print out its MAC address
            String mac = "";
            for (byte b : devices[i].mac_address) {
                //mac地址6段，每段是8位，所以只保留低 8位，和0xff相与
                mac = mac + Integer.toHexString(b & 0xff) + ":";
            }
            System.out.println("MAC address:" + mac.substring(0, mac.length() - 1));
            //print out its IP address, subnet mask and broadcast address
            for (NetworkInterfaceAddress addr : devices[i].addresses) {
                System.out.println(" address:"+addr.address + " " + addr.subnet + " "+ addr.broadcast );
            }
        }
        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(devices[4], 1514, true, 20);
        Packet packet = jpcapCaptor.getPacket();
        System.out.println(packet);
        jpcapCaptor.close();

    }
}
