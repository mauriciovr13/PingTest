/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author aluno
 */
public class UDPClient {
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        
        String servidor = "ibiza.dcc.ufla.br";
        int porta = 5002;
        
        int rttReceive = 0;
        int pack = 1;
        
        InetAddress IPAddress = InetAddress.getByName(servidor);

        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);
        
        while (pack <= 10) {

            String sentence = "Enviando pacote " + pack;
            System.out.println(sentence);
            sendData = sentence.getBytes();
            //DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);

            System.out.println("Enviando pacote UDP para " + servidor + ":" + porta);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            clientSocket.receive(receivePacket);
            
            //System.out.println("Pacote UDP recebido...");

            String modifiedSentence = new String(receivePacket.getData());

            System.out.println("Texto recebido do servidor:" + modifiedSentence);
            
            //clientSocket.close();
            
            pack++;
        }
        clientSocket.close();
        System.out.println("Socket cliente fechado!");
    }
        
}

