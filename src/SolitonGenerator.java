import java.util.Random;

/**
 * Created on 04/11/15.
 */
public class SolitonGenerator {
    private static final double DEFAULT_DELTA = 0.01;
    private static final int DEFAULT_SEED = 1;
    /*
    probability that generator gives value d
    t(d) = 1/dM             for d = 1,2,...M-1
    t(d) = ln(R/delta)/M    for d = M
    t(d) = 0                for d = M+1, ... N

    from https://www.ma.utexas.edu/users/voloch/Homework/MacKay05.pdf
    and the wikipedia page on soliton generators.
    */
    Random random;
    int k; // number of blocks
    int m; // probability spike. something between 0 < m < k
    double delta; // probability of failure (between 0, 1)
    double normalizer; // normalization factor
    double c;   // something 0 < c < 1
    double R;   // the expected number of 1s we are gunning for.


    public SolitonGenerator(int k, double c, int seed){
        this(k, c, DEFAULT_DELTA, seed);
    }
    public SolitonGenerator(int k, double c){
        this(k, c, DEFAULT_DELTA, DEFAULT_SEED);
    }
    public SolitonGenerator(int k, double c, double delta, int seed){
        this.k = k;
        this.c = c;
        this.delta = delta;
        random = new Random(seed);
        R = c * Math.log(k/delta) * Math.sqrt(k); // ideal from
        m = (int)Math.floor(k/R);                 // most should be around m
        normalizer = genNormalizer(); // this is why i wish i paid attention in stats
    }

    // randomly generate something that fits this pdf.
    public int generate(){
        double r = random.nextDouble();
        int d = 1;
        double sum = 0;
        while(sum <= r){
            sum += (idealSoliton(d) + robustSoliton(d))/ normalizer;
            d++;
        }
        return d-1; // darn ++, this is why we weren't getting any 1s q_q
    }

    // normalization value. this way ideal+robust/genNormalizer is a pdf that adds to 1 properly.
    public double genNormalizer(){
        double ret = 0;
        for(int i = 0; i < k; i++){
            ret += idealSoliton(i+1)+robustSoliton(i+1); // dangit probability starts at 1.
        }
        return ret;
    }

    public double robustSoliton(int d){
        if(d >= 1 && d <= m-1) return 1.0/(m*d);
        else if (d == m) return Math.log(R/delta)/m;
        else return 0;
    }
    // a probability density function
    // 1/k chance of being 1, otherwise 1/(d(d-1))
    public double idealSoliton(int d){
        return (d==1)? 1.0/k : 1.0/(d*(d-1));
    }
}
