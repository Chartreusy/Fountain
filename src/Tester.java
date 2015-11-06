import java.io.File;
import java.io.PrintWriter;

/**
 * Created on 04/11/15.
 */
public class Tester {

    public static void main(String[] args) throws Exception{
        //generator();
        //encoder();
        decoder();
        //xorTest();
    }
    public static void decoder() throws Exception{
        Encoder encoder = new Encoder("resources/TestData.txt", "resources/encoded.txt");
        Decoder decoder = new Decoder("resources/decoded.txt");
        decoder.decode(encoder.encode());


    }

    public static byte[] xor(byte[] one, byte[] two){
        int j = 0;
        byte[] ret = new byte[one.length];
        for(byte b : one){
            one[j] = (byte)(b^two[j++]);
        }
        return one;
    }

    public static void xorTest(){
        int j = 0;
        byte[] one = "hello".getBytes();
        byte[] two = "world".getBytes();
        byte[] three = xor(one, two);
        System.out.println("three: " + new String(three));
        byte[] four = xor(three, one);
        System.out.println("four: " + new String(four));
        byte[] five = xor(three, two);
        System.out.println("five: " + new String(five));

    }
    public static void generator() throws Exception{
        /*
        0.02:
        */
        PrintWriter pw = new PrintWriter(new File("resources/histogram3.txt"));
        int numPacks = 130;
        double c = 0.05;
        for(int i = 0; i< 10; i++){

            for(int j = 0; j<19; j++){
                pw.print("k:"+numPacks+",c:"+c+";");
                SolitonGenerator solitonGenerator = new SolitonGenerator(numPacks, c);
                int[] count = new int[6];
                for(int k = 0; k< numPacks; k++){
                    int gen = solitonGenerator.generate();
                    if(gen >= 5){
                        count[5]++;
                    } else {
                        count[gen] ++;
                    }

                }
                for(int k = 0; k< count.length; k++){
                    if(count[k] != 0) pw.print(k + ": " + (int)(((double)count[k]/(double)numPacks)*100) + "%, ");
                }
                c += 0.05;
                pw.println();
            }
            numPacks += 50;
            c = 0.05;
        }
        pw.flush();
        pw.close();

    }
    public static void encoder() throws Exception{
        Encoder encoder = new Encoder("resources/TestData.txt", "resources/encoded.txt");
        encoder.encode();
    }
}
