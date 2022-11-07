package se.drutt;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;

public class SHT30
{
    public final static int SHT_ADDRESS = 0x44;
    public final static byte[] FETCH_DATA = { (byte)0xE0, (byte)0x00 };
    public final static byte[] SINGLE_READ_CLOCK_STRETCH = { (byte)0x2C, (byte)0x06 };
    public final static byte[] CONTINUOUS_DATA_READ_05_LOW = { (byte)0x20, (byte)0x2F };

    private final I2C device;

    public static void main(String[] args)
    {
        Context pi4j = Pi4J.newAutoContext();

        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("SHT30")
                .bus(1)
                .device(SHT_ADDRESS)
                .provider("pigpio-i2c")
                .build();
        I2C sht30Dev = pi4j.create(i2cConfig);
        SHT30 sht30 = new SHT30(sht30Dev);

        sht30.activateContinuousRead();
        for (int i = 0; i < 10; i++)
        {
            try { Thread.sleep(2000); }
            catch (InterruptedException e) { throw new RuntimeException(e); }

            sht30.fetchData();
        }
    }

    public SHT30(I2C sht30Dev)
    {
        device = sht30Dev;
    }


    @SuppressWarnings("unused")
    public void singleShotRead()
    {
        if (device == null)
            return;

        try
        {
            device.write(SINGLE_READ_CLOCK_STRETCH,0,2);
        }
        catch(Exception ex){ex.printStackTrace();}

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byte[] data = new byte[6];
        device.read(data, 0, 6);

        SHT30Measurement measurement = new SHT30Measurement(data);

        System.out.println(measurement);
    }

    public void activateContinuousRead()
    {
        if (device == null)
            return;

        try {
            device.write(CONTINUOUS_DATA_READ_05_LOW, 0, 2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public  void fetchData()
    {
        byte[] data = new byte[6];

        try
        {
            device.write(FETCH_DATA,0,2);
            device.read(data,0,6);
        }
        catch(Exception ex){ex.printStackTrace();}

        SHT30Measurement measurement = new SHT30Measurement(data);
        System.out.println(measurement);
    }
}






