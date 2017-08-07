package com.example.team1.interface_team1;

/**
 * Created by s-tutida on 2017/08/06.
 */

/**
 * Listens for alerts about steps being detected.
 */
public interface StepListener {

    /**
     * Called when a step has been detected.  Given the time in nanoseconds at
     * which the step was detected.
     */
    public void step(long timeNs);

}