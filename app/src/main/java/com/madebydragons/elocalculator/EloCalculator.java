package com.madebydragons.elocalculator;


public class EloCalculator {

    private KFactor mK;

    public EloCalculator(KFactor k){ mK = k;}

    // a looses to b
    public  int loose(int a, int b){
        return new Double(points(a,b,0)).intValue();
    }

    // a wins to b
    public int win(int a, int b){
        return new Double(points(a,b,1)).intValue();

    }

    // a draw with b
    public  int draw(int a, int b){
        return new Double(points(a,b,0.5)).intValue();
    }

    // final elo of a given matches such that in elos[i] the outcome, in points was outcomes[i]
    public  double tournament(int initial, Integer elos[],Double outcomes[]) throws InvalidTournamentData{
        if(elos.length != outcomes.length)
            throw new InvalidTournamentData("inconsistent elo/outcomes lengths.");
        double p = 0.0;
        for(int i=0;i<elos.length;i++) p += points(initial, elos[i].intValue(), outcomes[i].doubleValue());
        return Math.round(  p + (double) initial );
    }


    public double points(double p1,double p2,double actual){
        double expected_p1 = 1.0 / (1 + Math.pow(10.0, (p2-p1)/400.0));
        double k = mK.K((int)Math.round(p1));
        //TODO: verify Math.round rounding is appropriate
        return Math.round(k*(actual - expected_p1));
    }
}

