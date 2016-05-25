package com.madebydragons.elocalculator;
import java.util.HashMap;

/**
 * A factory that creates an appropriate KFactor class based on a unique identifier.
 * Ids defined in strings.xml
 *
 */
public class KFactorFactory {

    public static String STANDARD_K_FACTOR_CHESS_COM   = "chess.com";
    public static String STANDARD_K_FACTOR_ICC         = "icc";
    public static String STANDARD_K_FACTOR_APPROX_FIDE = "approx_fide";
    public static String STANDARD_K_FACTOR_APPROX_USCF = "approx_uscf";
//    private Context mContext;
//
//    public KFactorFactory(Context c){
//        mContext = c;
//    }

    public static KFactor createKFactor(final double value) throws InvalidStaticKFactorValueException {
        if(value<0 || value>9999)
            throw new InvalidStaticKFactorValueException("Negative or too large a value:"+value);

        return new KFactor(){
            @Override
            public double K(int elo){
                return value;
            }
        };


    }

   public static KFactor createKFactor(String id) throws UnknownKFactorIdentifierException {

       if(id.equals(STANDARD_K_FACTOR_CHESS_COM))
/// /        if(id.equals(mContext.getString(R.string.k_factor_chess_com)))
            return new KFactor(){
                @Override
                public double K(int elo){
                    return 16.0;
                }
            };
       if(id.equals(STANDARD_K_FACTOR_ICC))
           return new KFactor(){
               @Override
               public double K(int elo){
                   return 32.0;
               }
           };
       if(id.equals(STANDARD_K_FACTOR_APPROX_FIDE))
           return new KFactor() {
               @Override
               public double K(int elo) {
                   if(elo<2400)
                       return 20.0;
                   else
                       return 10.0;
               }
           };
       if(id.equals(STANDARD_K_FACTOR_APPROX_USCF))
           return new KFactor() {

               @Override
               public double K(int elo) {

                   HashMap<Integer,Double> uscf = new HashMap<Integer, Double>();

                   // Table from http://www.uschess.org/content/view/12201/141/
                   uscf.put(200,68.36);uscf.put(300,66.46);uscf.put(400,64.51);uscf.put(500,62.51);
                   uscf.put(600,60.45);uscf.put(700,58.32);uscf.put(800,56.13);uscf.put(900,53.88);
                   uscf.put(950,52.73);uscf.put(1000,51.56);uscf.put(1050,50.38);uscf.put(1100,49.17);
                   uscf.put(1150,47.95);uscf.put(1200,46.71);uscf.put(1250,45.46);uscf.put(1300,44.18);
                   uscf.put(1350,42.89);uscf.put(1400,41.58);uscf.put(1450,40.24);uscf.put(1500,38.89);
                   uscf.put(1550,37.53);uscf.put(1600,36.14);uscf.put(1650,34.74);uscf.put(1700,33.32);
                   uscf.put(1750,31.88);uscf.put(1800,30.43);uscf.put(1850,28.97);uscf.put(1900,27.5);
                   uscf.put(1950,26.02);uscf.put(2000,24.53);uscf.put(2050,23.05);uscf.put(2100,21.59);
                   uscf.put(2150,20.14);uscf.put(2200,18.73);uscf.put(2220,18.18);uscf.put(2240,17.64);
                   uscf.put(2260,17.11);uscf.put(2280,16.59);uscf.put(2300,16.09);uscf.put(2320,15.61);
                   uscf.put(2340,15.15);uscf.put(2360,14.81);uscf.put(2380,14.81);uscf.put(2400,14.81);
                   uscf.put(2420,14.81);uscf.put(2440,14.81);uscf.put(2460,14.81);uscf.put(2480,14.81);
                   uscf.put(2500,14.81);uscf.put(2600,14.81);uscf.put(2700, 14.81);

                   //find closest ranking and use it
                   int approx_20 = (elo/20)*20;
                   int approx_50 = (elo/50)*50;
                   int approx_100 = (elo/50)*50;

                   if(uscf.get(approx_20)!=null) return uscf.get(approx_20);
                   if(uscf.get(approx_50)!=null) return uscf.get(approx_50);
                   if(uscf.get(approx_100)!=null) return uscf.get(approx_100);
                   if(approx_20<200) return uscf.get(200);
                   return uscf.get(2700);
               }
           };
       else throw new UnknownKFactorIdentifierException(id);
   }
}
