package se.drutt.greenhouse.controller;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

public class BlinkPin
{
    private final DigitalOutput output;
    private boolean timeToStop;

    public BlinkPin(Context pi4j, int pin)
    {
        this.output = pi4j.dout().create(pin);
        output.state(DigitalState.HIGH);
    }

    public void start()
    {
        timeToStop = false;
        ToggleThread toggleThread = new ToggleThread();
        toggleThread.start();
    }

    public void stop()
    {
        timeToStop = true;
    }

    public String toString()
    {
        return "CURRENT DIGITAL OUTPUT [" + output + "] STATE IS [" + output.state() + "]";
    }

    private class ToggleThread extends Thread
    {
        public void run()
        {
            while (!timeToStop)
            {
                try { sleep(500); } catch (InterruptedException e) { throw new RuntimeException(e); }
                output.toggle();
            }
        }
    }
}
