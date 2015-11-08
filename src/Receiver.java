import java.io.IOException;
import java.net.*;

/**
 * Created on 05/11/15.
 */
public class Receiver implements Runnable{
    private Decoder decoder;
    private String senderAddr;
    private int senderUDPPort, myUDPPort;

    public Receiver(String out, String senderAddr, int senderUDPPort)throws Exception {
        this(out, senderAddr, senderUDPPort, 1111);
    }

    public Receiver(String out, String senderAddr, int senderUDPPort, int myUDPPort) throws Exception{
        this(out);
        this.senderAddr = senderAddr;
        this.senderUDPPort = senderUDPPort;
        this.myUDPPort = myUDPPort;
    }

    public Receiver(String out) throws Exception{
        decoder = new Decoder(out);
    }
    @Override
    public void run(){
        NetworkReceiveThread nrt = new NetworkReceiveThread(decoder, senderAddr, senderUDPPort, senderUDPPort);
        new Thread(nrt, "NetworkReceiveThread").start();
        while(!decoder.complete()){
            decoder.run();
            // separate thread is pushing packets.
            // do we need to buffer them?
        }
        try{
            decoder.dump();
        }catch(Exception e){ e.printStackTrace(); }
        System.out.println("Decoder complete : " + decoder.received + " packets received.");
    }

    class NetworkReceiveThread implements Runnable{
        Decoder decoder;
        private DatagramSocket myUDPSocket;
        private String senderAddr;
        private int senderUDPPort, myUDPPort;

        public NetworkReceiveThread(Decoder decoder, String senderAddr, int senderUDPPort, int myUDPPort) {
            this.decoder = decoder;
            this.myUDPPort = myUDPPort;
            this.senderAddr = senderAddr;// for the ACK
            this.senderUDPPort = senderUDPPort;
            try{
                this.myUDPSocket = new DatagramSocket(this.myUDPPort);
            }catch(SocketException e){
                e.printStackTrace();
                System.exit(-1);
            }
        }
        @Override
        public void run(){
            try{
                while(!decoder.complete()){
                    byte[] recBuffer = new byte[Packet.PACKET_SIZE];
                    DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                    myUDPSocket.receive(recPacket);

                    Packet p = new Packet(recBuffer);
                    decoder.pushPacket(p);
                }

                // on completion, send back an ack repeatedly until we get a ack back.
                byte[] sendBuffer = new byte[]{1};
                DatagramPacket ackPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                                                               InetAddress.getByName(senderAddr),
                                                               senderUDPPort);
                byte[] recBuffer = new byte[1];
                DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                boolean sendAck = true;
                myUDPSocket.send(ackPacket);
                while(sendAck){
                    try{
                        myUDPSocket.receive(recPacket);
                        sendAck = false;
                    }catch(SocketTimeoutException e){
                        System.out.println("Ack response timeout. Ack'ing again.");
                        myUDPSocket.send(ackPacket);
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
