package ifts.inventario;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class InventarioApp extends Application<InventarioConfig> {
    
    public static void main(String[] args) throws Exception {
        new InventarioApp().run(args);
    }
    
    @Override
    public void run(InventarioConfig configuration, Environment environment) {
        final Inventario risorsaInventario = new Inventario(
                configuration.getDescrizione(),
                configuration.getQuant()
        );
        
        environment.jersey().register(risorsaInventario);
    }
}