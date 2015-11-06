/**
 * Created on 06/11/15.
 */
public class SynchronousTester {

    public static void main(String[] args) throws Exception{
        Pipeline ppl = new Pipeline();
        new EncoderThread(ppl);
        new DecoderThread(ppl);
    }
    static class Pipeline{
        boolean send = false;
        boolean complete = false;
        byte[] data;
        public synchronized void send(byte[] data){
            if(complete) return;
            if(send){
                try{
                    wait();
                } catch(InterruptedException e) {e.printStackTrace();}
            }
            send = true;
            this.data = data;
            notify();
        }
        public synchronized byte[] receive(){
            if(complete) return null;
            if(!send){
                try{
                    wait();
                } catch(InterruptedException e) {e.printStackTrace();}
            }
            send = false;
            notify();
            return data;
        }
        public synchronized void complete(){
            System.out.println();
            complete = true;
            notify();
        }


    }
    static class EncoderThread implements Runnable{
        Pipeline ppl;
        Encoder encoder;
        public EncoderThread(Pipeline ppl) throws Exception{
            this.ppl = ppl;
            encoder = new Encoder("resources/TestData.txt", "resources/encoderThread.txt");
            new Thread(this, "Encoder").start();
        }
        @Override
        public void run() {
            while(!ppl.complete){
                Packet p = encoder.genLTCode();
                ppl.send(p.toByteArray());
            }
            System.out.println("Encoder complete : " + encoder.count + " of " + encoder.packets.size() +" packets sent.");
        }
    }
    static class DecoderThread implements Runnable{
        Pipeline ppl;
        Decoder decoder;
        public DecoderThread(Pipeline ppl) throws Exception {
            this.ppl = ppl;
            decoder = new Decoder("resources/decoderThread.txt");
            new Thread(this, "Decoder").start();
        }
        @Override
        public void run(){
            while(!decoder.complete()){
                decoder.run();
                decoder.pushPacket(new Packet(ppl.receive()));
            }
            ppl.complete();
            try{
                decoder.dump();
            }catch(Exception e){ e.printStackTrace(); }
            System.out.println("Decoder complete : " + decoder.received + " packets received.");
        }
    }
}
