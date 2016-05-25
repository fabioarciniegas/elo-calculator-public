package com.madebydragons.elocalculator;

import junit.framework.TestCase;
import com.madebydragons.elocalculator.KFactorFactory;

//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;


public class KFactorFactoryTest extends TestCase {

   // @Before
    public void setUp() throws Exception {

    }

 //   @After
    public void tearDown() throws Exception {

    }

    //TODO: do a little scaffolding to get the identifier strings from R.strings instead
    //      of hardcoding them into the test.
 //   @Test
    public void testCreateKFactor() throws Exception {



            KFactor k;

            k = KFactorFactory.createKFactor(KFactorFactory.STANDARD_K_FACTOR_APPROX_FIDE);
            assertNotNull(k);
            k = KFactorFactory.createKFactor(KFactorFactory.STANDARD_K_FACTOR_APPROX_USCF);
            assertNotNull(k);
            k = KFactorFactory.createKFactor("chess.com");
            assertNotNull(k);
            k = KFactorFactory.createKFactor("icc");
            assertNotNull(k);


    }
}