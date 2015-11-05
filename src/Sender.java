/**
 * Created on 05/11/15.
 */
public class Sender {
    private Encoder encoder;
    public Sender(String in, String logPath) throws Exception{
        encoder = new Encoder(in, logPath);

    }
    public void run(){
        /*
        while(){ // decoder has not sent the completion message
            Packet p = genLTCode();
            // DatagramPacket dgp = new DatagramPacket(p.toByteArray(), ...);
            // send...
            // listen for completion. If nothing found, then send next.
        }
        */
    }
}
