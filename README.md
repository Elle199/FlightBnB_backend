# FlightBnB
This is the frontend part of a whole project, the backend can be found
[here](https://github.com/Elle199/FlightBnB_backend) and the backend is required
for the frontend to function as intended.
## Backend
Some the backend does require some setup to function and this is due to
file paths not being consistent between operating systems and server versions.
We used GlassFish Server 4.1 and 4.1.1 which treated paths in two different ways.
### Google Flight
In the `getFlights()` method there is only really one thing to change and
that is totally optional.<br>On line 41 you'll find `webClient.waitForBackgroundJavaScript(60 * page.getWebResponse().getLoadTime());`
and the static value here is a multiplyer to wait before trying to access the
rest of the information on the page, values returned from `getWebResponse()` is
in milliseconds. This is because there is no way to wait for background
scripts in `HTMLUnit` that we could find, and this page loads its content
dynamicly through scripts when the page is opened.
### AirBnB
The `getBnB()` requires you to define the path to `phantom.exe`,
which is provided in the backend repository, and the provided `getAirbnb.js`
file which both are located in `phantomjs/`. You have to gett the `jquery.min.js` in the same folder as
your `getAirbnb.js` file or else script will not function. Lastly you need
to find where on your local systme the ```output.json``` is placed. This is the
part that depends on your system. In our testing if you are using GlassFish
Server 4.1 this will be under the same location as `getAirbnb.js` is
executed from. But in GlassFish Server 4.1.1 it seems as if it is located
under <br> `C:\Users\[user]\AppData\Roaming\NetBeans\[version]\config\GF_4.1.1\[domain_name]\config\`.<br>
This could be due to how the servers are setup or it is just
different between version.<br><br> If the file is not located in any of these locations
try to run the script from your console of choice and make sure finishes. If it
does not you can try to understand why yourself or contact @Elle199.
## Frontend
The page need to run on a local webserver. We used `atom-live-server` for this
and it has worked for both of us.
