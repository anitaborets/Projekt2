import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class VatResponse {
@JsonProperty (value = "last_updated")

    String lastUpdated;
    String disclaimer;
    Map<String, StateTax> rates = new HashMap<>();

    public String getUpdate() {
        return lastUpdated;
    }

    public void setUpdate(String update) {
        this.lastUpdated = update;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Map<String, StateTax> getRates() {
        return rates;
    }

    public void setRates(Map<String, StateTax> rates) {
        this.rates = rates;
    }
}

