import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created on 04/11/15.
 */
public class Packet {
    int n;
    int d;
    HashSet<Integer> ind;
    byte[] data;

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
