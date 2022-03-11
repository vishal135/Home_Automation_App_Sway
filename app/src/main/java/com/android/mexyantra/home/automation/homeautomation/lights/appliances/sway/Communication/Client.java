package com.android.mexyantra.home.automation.homeautomation.lights.appliances.sway.Communication;

import android.os.StrictMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client implements Runnable{
    private final static String SERVER_ADDRESS = "192.168.43.223"; //"192.168.4.1";//public ip of my server
    private final static int SERVER_PORT = 4210;
    private byte[] bufApplianceNameRoomId; //used to sending information to esp is a form of byte

    public Client(byte[] bufApplianceNameRoomId){
        this.bufApplianceNameRoomId = bufApplianceNameRoomId;
        // this is for thread policy the AOS doesn't allow to transfer data using wifi module so we take the permission
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public void run(){

        InetAddress serverAddress;
        DatagramPacket packetApplianceNameRoomId;
        DatagramSocket socket;


        try {
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            socket = new DatagramSocket(); //DataGram socket is created
            packetApplianceNameRoomId = new DatagramPacket(bufApplianceNameRoomId, bufApplianceNameRoomId.length, serverAddress, SERVER_PORT);//Data is loaded with information where to send on address and port number
            socket.send(packetApplianceNameRoomId);//Data is send in the form of packets
            socket.close();//Needs to close the socket before other operation... its a good programming
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
