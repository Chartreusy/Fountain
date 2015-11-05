/**
 * Created on 05/11/15.
 */
public class Receiver {
    private Decoder decoder;
    public Receiver(String out) throws Exception{
        decoder = new Decoder(out);

    }
    public void run(){
        while(!decoder.complete()){
            decoder.run();
            if(false){// if there's a datagrampacket waiting for us
                // receive the dgp and convert it
                //Packet p;
                //decoder.pushPacket(p.fromByteArray(););
            }
        }
    }
}
