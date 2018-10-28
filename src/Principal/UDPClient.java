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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mauricio Vieira e Pedro Pio
 */
public class UDPClient {
    
    private static final int MAX_TIMEOUT = 250;
    private static final int QTD_PACOTES_ENVIADOS = 20;
    
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        //Criando o mecanismo de comunicação entre os programas de cliente e servidor
        DatagramSocket clientSocket = new DatagramSocket();
        //setando o timeout
        clientSocket.setSoTimeout(MAX_TIMEOUT);
        
        String servidor = "ibiza.dcc.ufla.br";
        int porta = 5002;
        
        InetAddress IPAddress = InetAddress.getByName(servidor);   
        //System.out.println(IPAddress);
        
        long rttReceive = 0;                //Tempo de resposta
        int qtdPacksReceive = 0;            //Quantidade de pacotes recebidos
        int pack = 1;                       //Numero do pacote atual
        long max = 0, avg = 0, min = 251;   //Tempo minino, maximo e a medio para recebimento da resposta
        long start = 0;                     //Tempo na hora do envio do pacote
        long end = 0;                       //Tempo na hora do recebimento do pacote
        long totalTime = 0;                 //Tempo total gasto para enviar todos os pacotes
        
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        
        while (pack <= QTD_PACOTES_ENVIADOS) {
                
            String sentence = "pacote " + pack;

            sendData = sentence.getBytes();
            //criando o datagrama do pacote que será enviado
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);

            System.out.println("Enviando " + sentence + " UDP para " + servidor + ":" + porta);
            
            //obtendo o tempo inicial
            start = System.nanoTime();
            
            //enviando o datagrama
            clientSocket.send(sendPacket);
            
            try {
                //criando o datagrama do pacote a ser recebido
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                
                clientSocket.receive(receivePacket);
                end = System.nanoTime(); //obtendo o tempo final
                
                rttReceive = (end - start)/1000000; //convertendo para ms
                //System.out.println(rttReceive);
                
                if (rttReceive < min) min = rttReceive;
                if (rttReceive > max) max = rttReceive;
            
                //System.out.println("Pacote UDP recebido...");
                
                totalTime += rttReceive;

                String modifiedSentence = new String(receivePacket.getData());

                System.out.println("Recebido o " + modifiedSentence + " no tempo de " + rttReceive);
                
                qtdPacksReceive++;
                
                Thread.sleep(1000 - rttReceive);

                //clientSocket.close();
            } catch (IOException | InterruptedException e) {
                    end = System.nanoTime();
                    long tempoAux = (end - start)/1000000;
                    System.out.println("Error, tempo gasto foi de "+ tempoAux);
                try {
                    Thread.sleep(1000 - tempoAux);
                } catch (InterruptedException ex) {
                    System.out.println("Erro na thread");
                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            pack++;
            
        }
        double min2 = min;
        double max2 = max;
        avg = totalTime/qtdPacksReceive;
        clientSocket.close();
        System.out.println("Socket cliente fechado!");
        float porcentagemPerda = 100 - ((100*qtdPacksReceive)/QTD_PACOTES_ENVIADOS);
        int lostPacks = QTD_PACOTES_ENVIADOS - qtdPacksReceive;
        //avg = totalTime/qtdPacksReceive;
        StringBuilder resultado = new StringBuilder();
        resultado.append("------------------------------------------------------------------------------------\n");
        resultado.append("Estatistica do ping para: ").append(IPAddress).append(" na porta ").append(porta).append("           \n");
        resultado.append("Pacotes: Enviados = ").append(QTD_PACOTES_ENVIADOS).append(", Recebidos = ").append(qtdPacksReceive).append(", Perdidos = ").append(lostPacks).append(" (").append(porcentagemPerda).append("% de perda)\n");
        resultado.append("Tempo aproximado em milissegundos: Mínimo = ").append(min).append(" ms, Máximo = ").append(max).append(" ms, Média = ").append(avg).append(" ms  \n");
        resultado.append("------------------------------------------------------------------------------------\n");
        System.out.println(resultado);
    }
        
}

