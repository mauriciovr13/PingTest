/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Principal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mauricio
 */
public class UDPClient {
    
    private static final int MAX_TIMEOUT = 250;
    private static final int QTD_PACOTES_ENVIADOS = 10;
    private Calendar cal;
    private SimpleDateFormat sdf;
    
    public void regLog(List<String> l) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("log.txt", true));
            cal = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:SS");
            bw.write(sdf.format(cal.getTime()));
            bw.newLine();
            for(String s : l) {
                bw.write(s);
                bw.newLine();
            }
            bw.newLine();
            
        } catch (IOException e) {
            System.out.println("Erro ao abrir o arquivo de log");
        } finally {    
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    System.out.println("Erro ao fechar o arquivo de log");
                }
            }
        
        }
    }
    
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(MAX_TIMEOUT);
        
        String servidor = "ibiza.dcc.ufla.br";
        int porta = 5002;
        
        InetAddress IPAddress = InetAddress.getByName(servidor);   
        //System.out.println(IPAddress);
        
        long rttReceive = 0;                //Tempo de resposta
        int qtdPacksReceive = 0;            //Quantidade de pacotes recebidos
        int pack = 1;                       //Numero do pacote atual
        long max = 0, avg = 0, min = 251;   //Tempo minino, maximo e a media para recebimento da resposta
        long start = 0;                     //Tempo na hora do envio do pacote
        long end = 0;                       //Tempo na hora do recebimento do pacote
        long totalTime = 0;                 //Tempo total gasto para enviar os pacotes
        
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        
        while (pack <= QTD_PACOTES_ENVIADOS) {
                
            String sentence = "pacote " + pack;
            //System.out.println("Enviando " + sentence);
            sendData = sentence.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, porta);

            System.out.println("Enviando " + sentence + " UDP para " + servidor + ":" + porta);

            //start = System.currentTimeMillis();
            start = System.currentTimeMillis();
            //System.out.println("start " + start);
            clientSocket.send(sendPacket);
            
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                
                clientSocket.receive(receivePacket);
                end = System.currentTimeMillis();                
                rttReceive = end - start;
                
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
                    end = System.currentTimeMillis();
                    long tempoAux = end - start;
                    System.out.println("Error, tempo gasto foi de "+ (end-start));
                try {
                    Thread.sleep(1000 - tempoAux);
                } catch (InterruptedException ex) {
                    System.out.println("Erro na thread");
                    Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            pack++;
            
        }
        clientSocket.close();
        System.out.println("Socket cliente fechado!");
        float porcentagemPerda = 100 - ((100*qtdPacksReceive)/QTD_PACOTES_ENVIADOS);
        int lostPacks = QTD_PACOTES_ENVIADOS - qtdPacksReceive;
        avg = totalTime/rttReceive;
        List<String> linhas = new ArrayList<String>();
        String linha = "------------------------------------------------------------------------------------";
        System.out.println(linha);
        linhas.add(linha);
        linha = "|Estatistica do ping para: " + IPAddress + " na porta " + porta + "           |";
        System.out.println(linha);
        linhas.add(linha);
        linha = "|Pacotes: Enviados = " + QTD_PACOTES_ENVIADOS + ", Recebidos = " + qtdPacksReceive + ", Perdidos = " + lostPacks + " ("+ porcentagemPerda + "% de perda)              |";
        System.out.println(linha);
        linhas.add(linha);
        linha ="|Tempo aproximado em milissegundos: Mínimo = " + min + " ms, Máximo = " + max + " ms, Média = " + avg + " ms  |";
        System.out.println(linha);
        linhas.add(linha);
        linha = "------------------------------------------------------------------------------------";
        System.out.println(linha);
        linhas.add(linha);
        
        //escrevendo no arquivo de logs
        new UDPClient().regLog(linhas);
        //System.out.println("------------------------------------------------------------------------------------");
        //System.out.println("|Estatistica do ping para: " + IPAddress + " na porta " + porta + "           |");
        //System.out.println("|Pacotes: Enviados = " + QTD_PACOTES_ENVIADOS + ", Recebidos = " + 
        //        qtdPacksReceive + ", Perdidos = " + lostPacks + " ("+ porcentagemPerda + "% de perda)              |");
        //System.out.println("|Tempo aproximado em milissegundos: Mínimo = " + min + " ms, Máximo = " + max + " ms, Média = " + 
        //        avg + " ms  |");
        //System.out.println("------------------------------------------------------------------------------------");
        
    }
        
}

