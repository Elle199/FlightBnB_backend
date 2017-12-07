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
   public Response getFlights(@QueryParam("place") String place, @QueryParam("depart") String dDate, @QueryParam("return") String rDate){
      return Response.ok(fBBean.getFlights(place, dDate, rDate)).build();
   }
   
   @GET
   @Path("bnb")
   @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
   public Response getEvents(@QueryParam("place") String filter, @QueryParam("in") String inDate, @QueryParam("out") String outDate) {
      return Response.ok(fBBean.getBnB(filter, inDate, outDate)).build();
   }   
}
