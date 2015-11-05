import java.io.*;
import java.util.*;

/**
 * Created on 04/11/15.
 */
public class Encoder {
    public static final int PACKET_SIZE  = 500;
    private static SolitonGenerator solitonGenerator;
    List<byte[]> packets = new ArrayList<byte[]>();
    public Encoder(String path) throws Exception{
        File file = new File(path);
        FileInputStream fileStream = new FileInputStream(file);
        fileStream.read();
        int fileSize = (int)file.length();

        int readLength = PACKET_SIZE;
        byte[] read;
        while(fileSize > 0){
            if(fileSize <= PACKET_SIZE){
                readLength = fileSize;
            }
            read = new byte[PACKET_SIZE];// zeroes even if readLength is short
            int sub = fileStream.read(read, 0, readLength);
            if(sub == -1) break;
            fileSize -= sub;
            packets.add(read);
        }
        fileStream.close();

        solitonGenerator = new SolitonGenerator(packets.size(), 0.8); // wtf should i put as c?
        // "a value somewhat smaller than 1 giving good results"
        // according to histogram2.txt i think 0.5 is pretty dece
        // 10% 1s and rest generally in range 2-5
        // if we go too high then we get nothing but 2s, really.
    }

    public LinkedList<Packet> encode() throws Exception{
        int k = packets.size();
        int n = 0;
        LinkedList<Packet> encoded = new LinkedList<Packet>();
        ArrayList<Integer> shuffler = new ArrayList<Integer>(k);
        for(int i = 0; i< k; i++){
            shuffler.add(i);
        }
        PrintWriter pw = new PrintWriter("resources/encoded.txt");
        int[] check = new int[k];
        while(n < 4*k){ // how many do we have to receive?
            //System.out.println("Packet #: " + n);
            Packet p = LTCodes(shuffler);
            for(Integer i : p.ind){
                check[i]++;
            }
            encoded.add(p);
            pw.println(p.toString());
            n++;
        }
        System.out.println("Sending: " + n + " of " + k);
        pw.flush();
        pw.close();
        String miss = "";
        int missing = 0;
        String rep = "";
        int repeats = 0;
        for(int i = 0; i< check.length; i++){
            if(check[i] == 0){
                missing++;
                miss += i + " ";
            }else if (check[i] >1){
                repeats++;
                rep = rep + i+" ";
            }
        }
        System.out.println(miss + " missing!");
        System.out.println(missing + " total missing!");
        //System.out.println(rep + " repeated!");
        //System.out.println(repeats + " total repeats!");

        for(Packet p : encoded){
            if(p.d != p.ind.size()){
                System.out.println(" ENCODER FAULT: " + p.d + ", " + p.ind.size());
            }
        }
        return encoded;
    }

    // generate tn
    // generates 1 packet.
    public Packet LTCodes(ArrayList<Integer> shuffler){
        int k = packets.size();
        int d = solitonGenerator.generate();
        //System.out.println("SolitonGeneration: " + d);
        Collections.shuffle(shuffler);
        byte[] t = new byte[PACKET_SIZE];
        HashSet<Integer> ind = new HashSet<Integer>();
        for(int i = 0; i< d; i++){
            int r = shuffler.get(i);
            ind.add(r);
            byte[] s = packets.get(r);
            int j = 0;
            for(byte b : t){
                t[j] = (byte)(b^s[j++]);
            }
        }
        return new Packet(k, d, ind, t);
    }
}
