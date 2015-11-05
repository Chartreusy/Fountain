import java.io.*;
import java.util.*;

/**
 * Created on 04/11/15.
 */
public class Decoder {
    public static final int PACKET_SIZE  = 500;
    Set<String> check = new HashSet<String>();
    // message queue
    HashMap<Integer, Packet> msgQ = new HashMap<Integer, Packet>();
    // packet buffer
    List<Packet> packetBuff = new LinkedList<Packet>();
    int totPackets = -1;
    String out;
    LinkedList<Packet> packetStream;

    public Decoder(String out) throws Exception{
        this.out = out;
    }

    public void decode(LinkedList<Packet> encoded) throws Exception{
        packetStream = encoded;
        File file = new File(out);
        PrintWriter pw = new PrintWriter(file);
        while(msgQ.size() != totPackets && packetStream.size() > 0){
            Packet p = packetStream.pop();
            LTDecode(p);
        }
        System.out.println(msgQ.size() + " accounted for.");
        for(int i = 0; i< totPackets; i++){
            if(msgQ.containsKey(i)){
                pw.print(new String(msgQ.get(i).data));
            } else {
                System.out.println(i + " is missing!");
            }
        }
        pw.flush();
        pw.close();
    }
    public void LTDecode(Packet p){
        if(totPackets == -1){
            totPackets = p.n;
            msgQ = new HashMap<Integer, Packet>(totPackets+1);
        }
        //if(check.contains(p.toSmallString())) return; else check.add(p.toSmallString());

        if(p.d == 1){
            int index = p.ind.iterator().next();
            System.out.println("Acquired: " + index);
            msgQ.put(index, p);
            if(msgQ.containsKey(index)) return;

            // xor against everything that contains it.
            for(Iterator<Packet> it = packetBuff.iterator(); it.hasNext();){
                Packet buffPacket = it.next(); // this should never be of length 1.
                if(buffPacket.ind.contains(index)){
                    if(match(buffPacket, p, null) == 1){
                        it.remove(); // if we've turned it into a single, then remove and recurse.
                        System.out.println("Push: " + buffPacket.toString());
                        packetStream.addFirst(buffPacket);
                        //LTDecode(buffPacket);
                    }
                }
            }
        } else{ // p.d > 1
            // check msgQ and xor any found out.
            for(Iterator<Integer> it = p.ind.iterator(); it.hasNext();){
                int index = it.next();
                Packet msg = msgQ.get(index);
                if(msg != null){
                    if(match(p, msg, it) == 1){
                        System.out.println("Recurse: " + p.toString());
                        //LTDecode(p); // go again but as a single now.
                        packetStream.addFirst(p);
                    }
                }
            }
            if(p.ind.size() >1){
                packetBuff.add(p);
            }
        }
    }

    // use cases:
    // 1. this is a single, p is a multiple. We want to xor this out of p.
    // 2. we are a multiple, p is a single, we want to xor p out of this.

    // returns resulting d
    // let's forsake the OOPness i guess.
    public int match(Packet big, Packet small, Iterator it){
        int id = small.ind.iterator().next(); // only value in that set.
        if(big.ind.contains(id)){
            big.data = xor(big.data, small.data); // remove small from big in every way.
            if(it == null) big.ind.remove(id);
            else it.remove();
            big.d--;
        }
        return big.d;
    }


    public List<Packet> extract() throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("resources/encoded.txt")));
        List<Packet> received = new ArrayList<Packet>();
        int k = Integer.parseInt(br.readLine());
        String s = br.readLine();
        while(s != null){
            received.add(new Packet(s));
            s = br.readLine();
        }
        return received;
    }

    public byte[] xor(byte[] one, byte[] two){ // should be guaranteed same length, can pad if necessary
        byte[] ret = new byte[one.length];
        int j = 0;
        for(byte b : one){
            ret[j] = (byte)(b^two[j++]);
        }
        return ret;
    }
}
