package utils;

import config.AppConfiguration;
import library.Rngs;
import library.Rvgs;

public class Distributions {

    private static Distributions instance = null;

    private Rvgs rvgs;
    private Rngs rngs;

    private Distributions() {
        rngs = new Rngs();
        rngs.plantSeeds(AppConfiguration.SEED);
        rvgs = new Rvgs(this.rngs);
    }

    /**
     * Return singleton instance
     */
    public static Distributions getInstance(){
        if (instance == null)
            instance = new Distributions();
        return instance;
    }

    public void selectStream(int index){
        rvgs.rngs.selectStream(index);
    }

    public double exponential(double param,int stream){
        selectStream(stream);
        return this.rvgs.exponential(1/param);
    }

    public double uniform(double a, double b){
        return rvgs.uniform(a, b);
    }

}

