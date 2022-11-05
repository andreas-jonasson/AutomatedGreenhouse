import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

public class SHT30
{
    public final static int SHT_ADDRESS = 0x44;

    public static void main(String[] args)
    {
        Context pi4j = Pi4J.newAutoContext();
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("SHT30").bus(1).device(SHT_ADDRESS).build();
        I2C sht30Dev = i2CProvider.create(i2cConfig);
        SHT30 sht30 = new SHT30(sht30Dev);
        sht30.fetchData(sht30Dev);
    }

    public SHT30(I2C sht30Dev)
    {
        setmode(sht30Dev);
    }

    public void setmode(I2C sht30)
    {
        byte[] command =  new byte[2];
        command[0] = (byte)0x27;
        command[1] = (byte)0x21;

        try
        {
            sht30.write(command,0,2);
        }
        catch(Exception ex){ex.printStackTrace();}
    }

    public  void fetchData(I2C sht30)
    {
        byte[] command =  new byte[2];
        byte[] b = new byte[6];
        command[0] = (byte)0xE0;
        //noinspection ConstantConditions
        command[1] = (byte)0x00;

        try
        {
            sht30.write(command,0,2);
            Thread.sleep(500);
            sht30.read(b,0,6);
        }
        catch(Exception ex){ex.printStackTrace();}


        int raw_TEMP  =  ( (b[0] & 0Xff) << 8 ) | ( b[1] & 0xff ) ;
        int raw_HUMID =  ( (b[3] & 0xff) << 8 ) | ( b[4] & 0xff );

        System.out.println("raw temp" + raw_TEMP);
        System.out.println("raw humid" + raw_HUMID);

        double humidity =  ( (double)raw_HUMID/0xffff) * 100 ;
        double tempC  =    ( (double)raw_TEMP /0xffff)  * 175  -  45 ;
        double tempF =     ( (double)raw_TEMP /0xffff)  * 315  -  49 ;

        System.out.println("HUMIDITY  : " + humidity + "%");
        System.out.println("TEMPRATURE  : " + tempC + " C");
        System.out.println("TEMPRATURE  : " + tempF + " F");
    }
}






