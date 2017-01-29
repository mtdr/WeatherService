package weather;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Condition;
import com.github.fedy2.weather.data.unit.DegreeUnit;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Julia on 20.10.2016.
 */
public class Weather {
    private Date date;
    private int temp;


    public Date showDate() {
        return date;
    }

    public int showTemp() {
        return temp;
    }

    public Weather() {
        this.date = null;
        this.temp = 0;
    }

    public Weather(String titleCity) throws JAXBException, IOException {
        YahooWeatherService service = new YahooWeatherService();
        List<Channel> channels = service.getForecastForLocation(titleCity, DegreeUnit.CELSIUS).first(1);
        for (Channel channel : channels) {
            this.date = channel.getItem().getCondition().getDate();
            this.temp = channel.getItem().getCondition().getTemp();
        }
    }
}
