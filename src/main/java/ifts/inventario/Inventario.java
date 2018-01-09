package ifts.inventario;

import java.net.URI;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

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
    public Response aggiungiProdotto(
        @QueryParam("codice") Optional<String> codice,
        @QueryParam("descrizione") Optional<String> descrizione,
        @QueryParam("quant") Optional<Integer> quant
    ) {
        // Se non è specificato alcun codice
        if(!codice.isPresent())
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Il parametro codice non può essere omesso.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        
        // Se codice è specificato come vuoto
        if(codice.get().isEmpty())
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Il parametro codice non può essere vuoto.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        
        // Se il codice è già presente 
        if(indiceProdotto(codice.get()) > -1)
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.CONFLICT)
                    .entity(codice + " già inserito.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();

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
        Prodotto p = new Prodotto(codice.get(),d,q);
        this.prodotti.add(p);
        
        // Restituisce "201 Created", con la URI del nuovo prodotto inserito
         URI pUri = UriBuilder.fromResource(Inventario.class)
                        .path(p.getCodice())
                        .build();
        return Response.created(pUri).build();
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
    public Response recuperaProdotto(@PathParam("codice") String codice) {
        // Cerca l'indice "i" del prodotto con codice "codice"
        int i = indiceProdotto(codice);
        
        // Se il prodotto non è presente
        if(i == -1)
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(codice + " non trovato.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        
        // Altrimenti, restituisce il prodotto desiderato
        return Response.ok(prodotti.get(i)).build();
    }
    
    @PUT
    @Path("/{codice}")
    public Response aggiornaProdotto(
        @PathParam("codice") String codice,
        @QueryParam("descrizione") Optional<String> descrizione,
        @QueryParam("quant") Optional<Integer> quant
    ) {
        // Se "quant" non è presente 
        if(!quant.isPresent())
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("'quant' non può essere omesso.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();

        
        // Se il prodotto non è presente
        int i = indiceProdotto(codice);
        if (i == -1)
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(codice + " non trovato.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        
        // Altrimenti, aggiorna il prodotto
        Prodotto vecchio = this.prodotti.get(i);
        String d = vecchio.getDescrizione();
        if(descrizione.isPresent()) d = descrizione.get();
        Prodotto nuovo = new Prodotto(codice,d,quant.get());
        this.prodotti.remove(i);
        this.prodotti.add(nuovo);
        // e restituisce 200 OK
        return Response.ok()
                .entity(nuovo)
                .type(MediaType.APPLICATION_JSON)
                .build();
        
    }
    
    @DELETE
    @Path("/{codice}")
    public Response eliminaProdotto(@PathParam("codice") String codice) {
        // Se il prodotto non è presente
        int i = indiceProdotto(codice);
        if (i == -1)
            // Restituisce un opportuno messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(codice + " non trovato.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        
        // Altrimenti, elimina il prodotto e restituisce 200 OK
        this.prodotti.remove(i);
        return Response.ok()
                .entity(codice + " eliminato.")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
