import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class CurrencyUtil {

    public static ArrayList<Currency> getCurrency() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection connection = url.openConnection();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Type type = new TypeToken<ArrayList<Currency>>() {
        }.getType();
        ArrayList<Currency> currencies = gson.fromJson(reader, type);
        return currencies;
    }

}
