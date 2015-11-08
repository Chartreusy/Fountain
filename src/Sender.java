import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created on 05/11/15.
 */
public class Sender implements Runnable{
    private Encoder encoder;
    private String receiverAddr;
    private int receiverUDPPort, myUDPPort;
    private DatagramSocket socket;

    public Sender(String in, String logPath, String receiverAddr, int receiverUDPPort, int myUDPPort) throws Exception{
        this(in, logPath);
        this.receiverAddr = receiverAddr;
        this.receiverUDPPort = receiverUDPPort;
        this.myUDPPort = myUDPPort;
        this.socket = new DatagramSocket(myUDPPort);
    }

    public Sender(String in, String logPath) throws Exception{
        encoder = new Encoder(in, logPath);

    }
    @Override
    public void run(){
        Boolean acked = false;
        AckWatcher watcher = new AckWatcher(socket, acked);
        new Thread(watcher, "AckWatcher").start();
        try{
            while(!acked){
                Packet p = encoder.genLTCode();
                byte[] b = p.toByteArray();
                DatagramPacket dgp = new DatagramPacket(b, b.length,
                                                        InetAddress.getByName(receiverAddr),
                                                        receiverUDPPort);
                socket.send(dgp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }


        /*
        while(){ // decoder has not sent the completion message
            Packet p = genLTCode();
            // DatagramPacket dgp = new DatagramPacket(p.toByteArray(), ...);
            // send...
            // listen for completion. If nothing found, then send next.
        }
        */
    }
    class AckWatcher implements Runnable{
        DatagramSocket socket;
        Boolean received;
        public AckWatcher(DatagramSocket socket, Boolean received){
            this.socket = socket;
            this.received = received;
        }
        @Override
        public void run(){
            boolean received = false;
            try{
                while(!received){
                    try{
                        socket.receive(new DatagramPacket(new byte[1], 1));
                        received = true;
                        this.received = true;
                    } catch(SocketTimeoutException e){}
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
