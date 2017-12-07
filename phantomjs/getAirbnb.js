var fs = require('fs');
var system = require('system');
var page = require('webpage').create();
page.settings.userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";
//sidans utseende kan ändras om fönstret är för litet (vissa objekt kan vara svårare att få tag på)
page.viewportSize = {width: 1280, height: 1024};
//Testar så urlen fungerar om jag sätter in egna värden
//Tanken var att jag skulle hämta dessa från inputen med id="to" och id="date"
var city = system.args[1];
var checkin = system.args[2];
var checkout = system.args[3];

page.onConsoleMessage = function(msg) {
  console.log(msg);
}

//öppnar sidan i en "osynlig" webbläsare
page.open('https://www.airbnb.se/s/' + city +'/homes?refinement_path=%2Fhomes&allow_override%255B%25=&allow_override%5B%5D=&checkin=' + checkin + '&checkout=' + checkout, function(status) {
  console.log("Status: " + status);
  console.log(system.args[1] + "\n" + system.args[2] + "\n" + system.args[3]);
  if(status === "success") {
  } else {
    console.log("Unable to load the adress!");
  }
  //för att jag ska kunna använda mig av jquery måste jag lägga in det på sidan
  page.injectJs('jquery.min.js');
  var residentsInfo = page.evaluate(function() {
    var residents = [];
    $("div[itemprop='itemListElement']").each(function(index, element) {
      var name1 = ($(element).find("._o0r6eqm").html());
      var type1 = $(element).find("._1127fdt6 span").html();
      type1 = type1.replace(/<(.*?)>/g, "");
      var price1 = $(element).find("._hylizj6").html();
      price1 = price1.replace(/<(.*?)>/g, "").replace("Pris", "");
      var rating1 = ($(element).find("._36rlri span[role='img']").attr("aria-label"));
      var image1 = ($(element).find("._1dp4576 div[role='img']").attr("style"));
      residents[index] = {name: name1, type: type1, price: price1, rating: rating1, image: image1};
    });
    //värderna för de olika boendena är sparade och returneras ihop
    return residents;
  });
  var path = 'output.json';
  //sparar informationen i en jsonfil
  fs.write(path, JSON.stringify(residentsInfo), 'w');
  phantom.exit();
});
