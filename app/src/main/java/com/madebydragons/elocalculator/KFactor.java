package com.madebydragons.elocalculator;

/**
 * Supposing Player A was expected to score E_A points but actually scored S_A points. The formula for updating his rating is R_A_1 = R_A + K(S_A - E_A).
 *
 * The K factor is the most commonly adjusted part of the ELO rating equation.
 * It is not modeled as a simple number because different people use different values and staggering scales.
 *
 * For example FIDE uses
 * K = 20, for players with a rating always under 2400.
 * K = 10, for players with any published rating of at least 2400 and at least 30 games played in previous events.
 *
 * For more information visit the wikipedia entry:
 * https://en.wikipedia.org/wiki/Elo_rating_system#Most_accurate_K-factor
 */
public abstract class KFactor {
    //For a given elo rating, return the appropriate K
    public abstract double K(int elo);
}
