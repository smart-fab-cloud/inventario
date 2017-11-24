package ifts.inventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/inventario")
@Produces(MediaType.APPLICATION_JSON)
public class Inventario {
    
    private String descrizioneDefault;
    private int quantDefault;
    private List<Prodotto> prodotti;
    
    public Inventario(String descrizioneDefault, int quantDefault) {
        this.descrizioneDefault = descrizioneDefault;
        this.quantDefault = quantDefault;
        this.prodotti = new ArrayList<Prodotto>();
    }
    
    @POST
    public void aggiungiProdotto(
        @QueryParam("codice") String codice,
        @QueryParam("descrizione") Optional<String> descrizione,
        @QueryParam("quant") Optional<Integer> quant
    ) {
        // Inserisce in "d" la descrizione passata come parametro (se c'è)
        // altrimenti lascia quella di default
        String d = descrizioneDefault;
        if(descrizione.isPresent()) 
            d = descrizione.get();
        // Inserisce in "q" la quantità passata come parametro (se c'è)
        // altrimenti lascia quella di default
        int q = quantDefault;
        if(quant.isPresent())
            q = quant.get();
        
        // Crea un nuovo prodotto e lo inserisce nella collezione di
        // prodotti memorizzati
        Prodotto p = new Prodotto(codice,d,q);
        this.prodotti.add(p);
    }
    
    // Metodo privato utilizzato per cercare l'indice del prodotto
    // avente un determinato "codice" all'interno della collezione
    // di "prodotti".
    private int indiceProdotto(String codice) {
        for(int i=0; i<this.prodotti.size(); i++)
            if(this.prodotti.get(i).getCodice().equals(codice))
                return i;
        return -1;
    }
    
    @GET
    @Path("/{codice}")
    public Prodotto recuperaProdotto(@PathParam("codice") String codice) {
        // Cerca l'indice "i" del prodotto con codice "codice"
        int i = indiceProdotto(codice);
        // Restituisce il prodotto desiderato (se presente)
        // altrimenti restituisce null
        if (i>=0)
            return prodotti.get(i);
        return null;
    }
    
    @PUT
    @Path("/{codice}")
    public void aggiornaProdotto(
        @PathParam("codice") String codice,
        @QueryParam("descrizione") Optional<String> descrizione,
        @QueryParam("quant") int quant
    ) {
        // Cerca l'indice "i" del prodotto con codice "codice"
        int i = indiceProdotto(codice);
        // Se tale prodotto è presente
        if (i>=0) {
            // Recupera la vecchia descrizione del prodotto (in posizione "i")
            // e la aggiorna con le nuove informazioni
            Prodotto vecchio = this.prodotti.get(i);
            String d = vecchio.getDescrizione();
            if(descrizione.isPresent())
                d = descrizione.get();
            Prodotto nuovo = new Prodotto(codice,d,quant);
            // Rimuove la vecchia descrizione del prodotto (in posizione "i")
            // e re-inserisce quella aggiornata
            this.prodotti.remove(i);
            this.prodotti.add(nuovo);
        }
    }
    
    @DELETE
    @Path("/{codice}")
    public void eliminaProdotto(@PathParam("codice") String codice) {
        // Cerca l'indice "i" del prodotto con codice "codice"
        int i = indiceProdotto(codice);
        // Elimina il prodotto dalla collezione di "prodotti"
        this.prodotti.remove(i);
    }
}
