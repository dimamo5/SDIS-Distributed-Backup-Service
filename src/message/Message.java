package message;

import service.MessageHandler;
import service.MessageSender;
import service.Peer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * Created by Sonhs on 13/03/2016.
 */
public class Message {

    Header header = null;
    byte[] body = null;

    //receives the byte[] from the packet.getData()
    public Message(DatagramPacket message){
        processMessage(message);
    }

    public Message(Header header){
        this.header = header;
    }

    public Message(Header header, byte[] data){
        this.header = header;
        this.body = data;
    }

    public Header getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public int getBodyLength(){
        return body.length;
    }

    public void processMessage(DatagramPacket message){

        if(processHeader(message) && processBody(message)){
            System.out.println("Message processed");
        } else {
            System.out.println("Error processing message");
            System.exit(1);
        }
    }

    private boolean processHeader(DatagramPacket message){
        InputStream is = new ByteArrayInputStream(message.getData());

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String header_fields;

        try {
            header_fields = reader.readLine(); //TODO: Considera-se que apenas a primeira linha de campos é a única relevante?
            System.out.println(header);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("header bytes: "+ new String(header_fields.getBytes()));

        this.header=new Header(header_fields.getBytes());
        return true;
    }

    private boolean processBody(DatagramPacket message) {
        InputStream is = new ByteArrayInputStream(message.getData());
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String ignore;
        int ignore_count = 0, ignored_length = 0; //ignored length matches the body beginning

        do {
            try {
                ignore = reader.readLine();
                ignored_length += ignore.length() + Header.CRLF.length();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            ignore_count++;
        }
        while(ignore.length() != 0 );

        /*extract body data
         *copyofrange reveals best performance in relation to new String(byte[]) + String.getChars() */
        if(ignored_length != message.getLength()) //TODO VERIFICAR SE ISTO NAO DÁ PEIDO PARA PACKETS COM HEADER ONLY
            body = Arrays.copyOfRange(message.getData(), ignored_length, message.getLength());

        return true;
    }

    public byte[] getMessageBytes(){

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(header.getHeaderMsg().length + (body != null ? body.length : 0));

        System.out.println("cenas");

        try {
            outputStream.write(header.getHeaderMsg());
            if(body!= null)
                outputStream.write(body);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error concatenating message's header+body byte[]");
            System.exit(1);
        }

       return outputStream.toByteArray();
    }

    @Override
    public String toString() {
        return "Message{" +
                "header=" + header.toString() +
                ", body=" + Arrays.toString(body) +
                '}';
    }

    public static void main(String args[]){

        Message m = new Message(new Header("tipox 1.0 omeuid sha256sha256sha256sha256sha256sh 2 2\r\n\r\n".getBytes()));
        InetAddress addres = Peer.getIp();

        DatagramPacket message = new DatagramPacket(m.getMessageBytes(),m.getMessageBytes().length,addres,10000);

        Message mx = new Message(message);

        System.out.println(mx.toString());
    }



}
