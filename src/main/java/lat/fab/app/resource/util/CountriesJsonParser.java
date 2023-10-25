package lat.fab.app.resource.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lat.fab.app.resource.util.countries.Country;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CountriesJsonParser {

    public static List<Country> fromJsonFileToList() {
        Path filePath = Path.of("../countries/countries-response.json");

        try {
            String jsonStr = Files.readString(filePath);
            return new ObjectMapper().readValue(jsonStr, List.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Country> fromListToMap(List<Country> countriesList) {
        return countriesList.stream()
                .collect(Collectors.toMap(Country::getCca3, Function.identity()));
    }
}
