package ifts.inventario;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class InventarioConfig extends Configuration {
    
    private String descrizione;
    private int quant;
    
    @JsonProperty
    public String getDescrizione() {
        return descrizione;
    }
    
    @JsonProperty
    public int getQuant() {
        return quant;
    }
    
}
