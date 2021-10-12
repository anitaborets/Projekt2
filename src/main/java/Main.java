import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static final String euvatrates = "https://euvatrates.com/rates.json";
    private static final String jsonFile = "user.json";


    public static void main(String[] args) throws Exception, RuntimeException {
        //ulozit json file
        downloadFile();

        //nacist json a ulozit do zoznamu objektov
        String body = readJson();
        VatResponse vat = mapToObject(body);

        List<StateTax> list = new ArrayList();
        int i = 0;
        for (String key : vat.rates.keySet()) {
            StateTax value = vat.getRates().get(key);
            list.add(i, value);
            value.setName(key);
            i++;
        }

        //vybrat minimalne sadzby DPH
        List<StateTax> list1;
        list1 = sortMinRates(list);
        print(list1);

        //vybrat maximalne sadzby DPH
        List<StateTax> list2;
        list2 = sortMaxRates(list);
        print(list2);

        //spojit zoznamy:minimalne a maximalne sadzby
        List<StateTax> list3 = new ArrayList<>();
        list3.addAll(list1);
        list3.addAll(list2);
        toFile(list3);

        //odoslat API
        sendAPI(list3);

        //vyhledavani podla statu od uzivatela
        getFromUser(list);

    }

    private static void downloadFile() throws Exception {
        try {
            URL url = new URL(euvatrates);
            InputStream input = url.openStream();
            Path path = Path.of("d:\\rates.json");
            if (Files.notExists(path)) {
                Files.copy(input, path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readJson() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(euvatrates)).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status code " + httpResponse.statusCode());
        //System.out.println("JSON " + httpResponse.body());
        return httpResponse.body();
    }

    private static VatResponse mapToObject(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VatResponse vatResponse = mapper.readValue(body, VatResponse.class);
        System.out.println("Count " + vatResponse.getRates().size());
        return vatResponse;
    }

    private static List<StateTax> sortMaxRates(List<StateTax> list) {
        List<StateTax> maxList = new ArrayList();
        Comparator tax = new StateTaxComparator();
        Collections.sort(list, tax);
        System.out.println("===========================");
        System.out.println("Státy, které maji 3 najvyšší sadzby DPH ");
        int max = list.get(0).standard_rate;
        int i = 0;
        for (StateTax stateTax : list) {
            if ((stateTax.standard_rate < max) && (i < 2)) {
                max = stateTax.getStandard_rate();
                i++;
                maxList.add(stateTax);

            } else if (stateTax.standard_rate == max) {
                maxList.add(stateTax);
            }
        }
        return maxList;
    }

    private static List<StateTax> sortMinRates(List<StateTax> list) {
        List<StateTax> minList = new ArrayList();
        Comparator tax = new StateTaxComparator();
        Collections.sort(list, tax);
        Collections.reverse(list);

        System.out.println("===========================");
        System.out.println("Státy, které maji 3 najmenší sadzby DPH ");
        int min = list.get(0).standard_rate;
        int i = 0;
        for (StateTax stateTax : list) {
            if ((stateTax.standard_rate > min) && (i < 2)) {
                min = stateTax.getStandard_rate();
                i++;
                minList.add(stateTax);
            } else if (stateTax.standard_rate == min) {
                minList.add(stateTax);
            }
        }

        return minList;


    }

    private static void getFromUser(List<StateTax> list) {
        String nameOfState;
        System.out.println("===========================");
        System.out.println("Zadajte kód zeme  :");
        Scanner console = new Scanner(System.in);
        nameOfState = console.nextLine();

        for (StateTax stateTax : list) {
            if (nameOfState.equalsIgnoreCase(stateTax.name)) {
                System.out.println("Sadzba DPH v state " + stateTax.country + " :" + stateTax.standard_rate + " %");
            }
        }

    }

    private static void print (List<StateTax> list) {
        for (StateTax stateTax : list) {
         System.out.println(stateTax);
                          }
        }

    private static void toFile (List<StateTax> list) throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(jsonFile), list);
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Json created");
    }

    private static void sendAPI (List<StateTax> list) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(list);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://postman-echo.com/post"))
                .headers("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();

        HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }

}





