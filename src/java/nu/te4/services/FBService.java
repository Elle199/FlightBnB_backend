package nu.te4.services;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nu.te4.beans.FBBean;

@Path("/")
public class FBService {
   @EJB
   FBBean fBBean;
   
   @GET
   @Path("flights")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getFlights(@QueryParam("place") String place){
      return Response.ok(fBBean.getFlights(place)).build();
   }
   
   @GET
   @Path("bnb")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getEvents(@QueryParam("filter") String filter) {
      return Response.ok(fBBean.getBnB(filter)).build();
   }   
}
