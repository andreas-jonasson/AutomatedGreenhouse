import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.provider.Provider;

public class SHT30
{
    public final static int SHT_ADDRESS = 0x44;
    public final static byte[] FETCH_DATA = { (byte)0xE0, (byte)0x00 };
    public final static byte[] SINGLE_READ_CLOCK_STRETCH = { (byte)0x2C, (byte)0x06 };
    public final static byte[] CONTINUOUS_DATA_READ_05_LOW = { (byte)0x20, (byte)0x2F };

    private I2C device;

    public static void main(String[] args)
    {
        System.setProperty("pi4j.library.path", "/usr/local/lib");
        Context pi4j = Pi4J.newAutoContext();
        //I2CProvider i2CProvider = pi4j.provider("pigpio-i2c");

        for (Provider provider : pi4j.providers().all().values())
            System.out.println("  " + provider.name() + " [" + provider.id() + "]; " + provider.type());

        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("SHT30")
                .bus(1)
                .device(SHT_ADDRESS)
                .provider("raspberrypi-i2c")
                .build();
        I2C sht30Dev = pi4j.create(i2cConfig);
        SHT30 sht30 = new SHT30(sht30Dev);
        //sht30.singleShotRead();
        sht30.activateContinuousRead();
        for (int i = 0; i < 10; i++)
        {
            sht30.fetchData();

            try { Thread.sleep(2000); }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
    }

    public SHT30(I2C sht30Dev)
    {
        device = sht30Dev;
    }

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

        // Read 6 bytes of data
        // Temp msb, Temp lsb, Temp CRC, Humididty msb, Humidity lsb, Humidity CRC
        byte[] data = new byte[6];
        device.read(data, 0, 6);

        // Convert the data
        int temp = ((data[0] & 0xFF) * 256) + (data[1] & 0xFF);
        double cTemp = -45 + (175 * temp / 65535.0);
        double fTemp = -49 + (315 * temp / 65535.0);
        double humidity = 100 * (((data[3] & 0xFF) * 256) + (data[4]  & 0xFF)) / 65535.0;

        // Output data to screen
        System.out.printf("Relative Humidity : %.2f %%RH %n", humidity);
        System.out.printf("Temperature in Celsius : %.2f C %n", cTemp);
        System.out.printf("Temperature in Fahrenheit : %.2f F %n", fTemp);
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
        byte[] b = new byte[6];

        try
        {
            device.write(FETCH_DATA,0,2);
            device.read(b,0,6);
        }
        catch(Exception ex){ex.printStackTrace();}


        int raw_TEMP  =  ( (b[0] & 0Xff) << 8 ) | ( b[1] & 0xff ) ;
        int raw_HUMID =  ( (b[3] & 0xff) << 8 ) | ( b[4] & 0xff );

        System.out.println("raw temp: " + raw_TEMP);
        System.out.println("raw humid: " + raw_HUMID);

        double humidity =  ( (double)raw_HUMID/0xffff) * 100 ;
        double tempC  =    ( (double)raw_TEMP /0xffff)  * 175  -  45 ;
        double tempF =     ( (double)raw_TEMP /0xffff)  * 315  -  49 ;

        System.out.println("HUMIDITY  : " + humidity + "%");
        System.out.println("TEMPRATURE  : " + tempC + " C");
        System.out.println("TEMPRATURE  : " + tempF + " F");
    }
}






