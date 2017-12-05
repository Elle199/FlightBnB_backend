package nu.te4.beans;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Stateless
public class FBBean {

   public JsonArray getFlights(String place) {
      String pageData = ""; //Will contain the page gotten through HtmlUnit
      String className; //Will contain main class name
      String classEnding = ""; //Will contain class ending
      JsonArrayBuilder flights = Json.createArrayBuilder(); //Store all flights

      try {
         //Sets up HtmlUnit browser
         WebClient webClient = new WebClient(BrowserVersion.CHROME);
         webClient.getOptions().setJavaScriptEnabled(true);
         webClient.setAjaxController(new NicelyResynchronizingAjaxController());

         HtmlPage page = webClient.getPage(String.format("https://www.google.com/flights/#search;f=%s;d=2017-12-03;r=2017-12-07;mc=e", place)); //Gets page :P
         System.out.println("Load time: " + page.getWebResponse().getLoadTime());
         webClient.waitForBackgroundJavaScript(25 * page.getWebResponse().getLoadTime()); //Waits for 25x response time to make sure page is fully loaded

         className = page.getByXPath("//div[@id='root']").get(0).toString();
         className = className.substring(className.indexOf('c'));
         className = className.substring(className.indexOf("\"") + 1, className.indexOf('-'));

         List<HtmlDivision> classFilterList = page.getByXPath(String.format("//div[contains(@class,'%s-')]", className)); //Only used for gettin class ending

         /*Loops through each div element with possibly wanted class and filters
         / out ending of class name*/
         String previousValue = "";
         for (HtmlDivision i : classFilterList) {
            Document currentDiv = Jsoup.parse(i.toString());
            String elementClass = currentDiv.select("div").get(0).attr("class");
            classEnding = getClassValue(elementClass, previousValue);

            if (!classEnding.equals("")) {
               previousValue = classEnding;
            }
            classEnding = previousValue; //Makes sure classEnding isn't empty
         }

         //Here starts actuall extraction of information from page
         String targetedElementClass = className + "-" + classEnding; //Stores the target class to use
         List<HtmlDivision> divs = page.getByXPath(String.format("//div[@class='%s-t']", targetedElementClass)); //Filters out all relevant divs //Previous DQX2Q1B-nb
         System.out.println("TargetClass: " + targetedElementClass);

         //Loops through relevant divs and adds XML infomation to 'data' 
         for (HtmlDivision i : divs) {
            pageData += i.asXml();
         }

         //Uses the 'data' String to create a Jsoup Document
         Document doc = Jsoup.parse(pageData, "UTF-8");

         /*Loops through each div and gathers all information in each card and 
           adds it to a list if information is relevant*/
         for (int i = 0; i < divs.size(); i++) {
            JsonObject flight = getFlight(doc, targetedElementClass, i);
            if (!flight.isEmpty()) {
               if (!flight.isNull("price")) {
                  flights.add(flight);
               }
            }
         }
      } catch (Exception ex) {
         System.out.println("Global code error: ");
         System.out.println(ex.getMessage());
         System.out.println(ex.getCause());
         System.out.println(ex.getLocalizedMessage());
      }
      System.out.println(flights);
      return flights.build();
   }

   public JsonObject getBnB(String url) {
      System.out.println("2");
      try {
         System.out.println("1");
         System.out.println(Runtime.getRuntime().exec("C:\\Users\\maxangman\\AppData\\Roaming\\npm\\node_modules\\phantomjs-prebuilt\\lib\\phantom\\bin\\phantomjs.exe C:\\Users\\maxangman\\Documents\\HTML-CSS\\#Projects#\\Hemsida\\js\\getAirbnb.js").waitFor(2, TimeUnit.MINUTES));
         BufferedReader bnbFile = new BufferedReader(new FileReader("C:\\Users\\maxangman\\AppData\\Roaming\\NetBeans\\8.2\\config\\GF_4.1.1\\domain1\\config\\output.json"));
         System.out.println(bnbFile);
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   //Extracts wanted information from privided elment
   public static JsonObject getFlight(Document doc, String targetClass, int i) {
      JsonObjectBuilder cFlight = Json.createObjectBuilder();

      Element currentCard = doc.select(String.format("div.%s-t", targetClass)).get(i);

      String locationName = currentCard.select(String.format("div.%s-u", targetClass)).html();
      String flightTime;
      String landings;
      String price;

      //Check if selector has content, if found add
      if (currentCard.select(String.format("span.%s-e", targetClass)).hasText() != false) {
         flightTime = currentCard.select(String.format("span.%s-e", targetClass)).html();
         if (flightTime.contains("+")) {
            flightTime = flightTime.replace("+", "");
         }
      } else {
         flightTime = "none"; //filling
      }

      //Check if selector has content, if found add
      if (currentCard.select(String.format("span.%s-s", targetClass)).hasText() != false) {
         landings = currentCard.select(String.format("span.%s-s", targetClass)).html();
      } else {
         landings = "none"; //filling
      }

      //Check if selector has content, if found add
      if (currentCard.select(String.format("span.%s-p", targetClass)).hasText() != false) {
         price = currentCard.select(String.format("div.%s-n span.%s-p", targetClass, targetClass)).html().replaceAll("[^0-9]+", "");
      } else {
         price = "0"; //Used as validation token
      }

      //Check if flight is valid before adding
      if (!price.equals("0")) {  //If a flight has no price don't use it
         cFlight = Json.createObjectBuilder().add("locationName", locationName)
                 .add("flightTime", flightTime).add("landings", landings)
                 .add("price", price);
      }
      JsonObject flight = cFlight.build();
      return flight;
   }

   //Extracts ending of class name
   public static String getClassValue(String elmntClass, String lastValue) {
      String lastClass = "";
      try {
         //Filters out '-' and ' ' in given class name
         if (elmntClass.contains(" ")) {
            elmntClass = elmntClass.substring(elmntClass.indexOf('-') + 1, elmntClass.indexOf(" "));
         } else if (elmntClass.contains("-")) {
            elmntClass = elmntClass.substring(elmntClass.indexOf('-') + 1);
         }

         //Removes possible unwanted extensions to wanted class value
         if (elmntClass.contains("-")) {
            elmntClass = elmntClass.substring(0, elmntClass.indexOf('-'));
         }

         //Saftey check to make sure no '-' exists
         if (elmntClass.contains("-")) {
            elmntClass = elmntClass.substring(0, elmntClass.indexOf('-'));
         }

         //if the String is the correct length save the value
         if (elmntClass.length() == 2) {
            lastClass = elmntClass;
            if (lastClass.equals("rb")) {
               lastClass = lastValue;
            }
         }
      } catch (Exception e) {
         System.out.println("Error getting class value");
         System.out.println(e.getMessage());
      }
      return lastClass;
   }
}
