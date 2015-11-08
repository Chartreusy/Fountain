import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created on 04/11/15.
 */
public class Packet {
    public static final int PACKET_SIZE  = 500;
    int n;
    int d;
    HashSet<Integer> ind;
    byte[] data;

    public Packet(){
        this.ind = new HashSet<Integer>();
    }
    public Packet(byte[] b){
        this();
        this.fromByteArray(b);
    }
    public Packet(int n, int d, HashSet<Integer> ind, byte[] data) {
        this.n = n;
        this.d = d;
        this.ind = ind;
        this.data = data;
    }

    public Packet(String s){
        this.ind = new HashSet<Integer>();
        fromString(s);
    }

    public byte[] xor(byte[] one, byte[] two){ // should be guaranteed same length, can pad if necessary
        byte[] ret = new byte[one.length];
        int j = 0;
        for(byte b : one){
            ret[j] = (byte)(b^two[j++]);
        }
        return ret;
    }




    // for datagrampacket
    public byte[] toByteArray(){
        byte[] ret = new byte[data.length+4*ind.size()+8];
        byte[] na = intToByteArray(n);
        byte[] da = intToByteArray(d);
        for(int i = 0; i< 4; i++){
            ret[i] = na[i];
            ret[i+4] = da[i];
        }
        int count = 0;
        // 4*ind.size space + 8
        for(Integer ii : ind){
            byte[] ia = intToByteArray(ii);
            for(int i = 0; i < 4; i++){
                ret[count*4 + 8 + i] = ia[i];
            }
            count++;
        }
        for(int i = 0; i< data.length; i++){
            ret[4*ind.size()+8+i] = data[i];
        }
        return ret;
    }
    public void fromByteArray(byte[] ba){
        this.n = byteArrayToInt(Arrays.copyOfRange(ba, 0, 4)); // n
        this.d = byteArrayToInt(Arrays.copyOfRange(ba, 4, 8)); // d
        for(int i = 0; i< this.d; i++){
            this.ind.add(byteArrayToInt(Arrays.copyOfRange(ba, i*4+8,i*4+12)));
        }
        this.data = Arrays.copyOfRange(ba, 4*ind.size()+8, ba.length);
    }

    // byte array of size 4, probably mostly zeroes.
    public byte[] intToByteArray(int i){
        return new byte[] {
                (byte)((i>>24) & 0xFF),
                (byte)((i>>16) & 0xFF),
                (byte)((i>>8) & 0xFF),
                (byte)((i) & 0xFF),
        };
    }
    public int byteArrayToInt(byte[] b){
        return (b[3] & 0xFF) | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }



    public String toSmallString(){
        String ret = "";
        ret = n + " " + d + " ";
        for(Integer i : ind){
            ret = ret + i + " ";
        }
        return ret;
    }
    public String toString(){
        String ret = "";
        ret = n + " " + d + " ";
        for(Integer i : ind){
            ret = ret + i + " ";
        }
        ret = ret + new String(data);
        return ret;
    }
    public void fromString(String s){
        String[] ss = s.split(" ");
        this.n = Integer.parseInt(ss[0]);
        this.d = Integer.parseInt(ss[1]);
        System.out.println(n + ", " + d);
        for(int i = 0; i< d; i++){
            ind.add(Integer.parseInt(ss[i + 2]));
        }
        this.data = ss[d+2].getBytes();
    }
}
