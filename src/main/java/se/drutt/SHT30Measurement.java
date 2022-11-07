package se.drutt;

import java.text.DecimalFormat;

public class SHT30Measurement
{
    private final double temperature;
    private final double humidity;

    public SHT30Measurement(byte[] data)
    {
        // Six bytes: {Temp msb, Temp lsb, Temp CRC, Humididty msb, Humidity lsb, Humidity CRC}
        int temp = ((data[0] & 0xFF) * 256) + (data[1] & 0xFF);

        temperature = -45 + (175 * temp / 65535.0);
        humidity = 100 * (((data[3] & 0xFF) * 256) + (data[4] & 0xFF)) / 65535.0;
    }

    @SuppressWarnings("unused")
    public double getTemperature() {
        return temperature;
    }

    @SuppressWarnings("unused")
    public double getHumidity() {
        return humidity;
    }

    @Override
    public String toString()
    {
        DecimalFormat df = new DecimalFormat("0.00");

        return "{\n" +
                    "\t\"temperature\": " + df.format(temperature) + ",\n" +
                    "\t\"humidity\": " + df.format(humidity) +  "\n" +
                '}';
    }
}
