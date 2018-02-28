# README

## REST examples

### JavaScriptForRest

Follow these steps.

`cd ~/JavaScriptForRest`\
`php -S localhost:8000`

In your default browser goto `http://localhost:8000/popcornA.html`\
or `http://localhost:8000/popcornA.html`\
or `http://localhost:8000/objectInheritance_Person1.html`

### MashupTest

This project demonstrates mashup from different services and converging the results into one.<br/>
If logging in remotely from laptop or home pc<br/>

`ssh -X uname_encs@computation.encs.concordia.ca`<br/>

else<br/>

Login to a encs lab pc and<br/>
`openesb --userdir /groups/alphabet/gname_487_x/temp/&`<br/>

In the services tab add a new server instance e.g. `GlassFish_Server`.<br/>
It can be downloaded on the go from within the IDE into your `/group-directory` or the `/nettemp-directory` .<br/>

For Apache Tomcat in the ENCS environment you need to use the wrapper command `tomcat7_setup /path/to/folder/that/will/become/CATALINE_BASE`<br/>

For example:<br/>
`[orwell][/groups/x/xyz_soen487_i] > tomcat7_setup xyzTomcat`<br/>
The output of the above command should look similar to the following,<br/>

`Subdirectory xyzTomcat created`

`Making local copy of tomcat configuration directory`

`Success!`

`Please add /encs/pkg/tomcat-7.0.78/root/bin to your path.`<br/>
`Please run the following commands:`<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`setenv     JAVA_HOME /encs/pkg/jdk-8/root`<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`setenv      JRE_HOME /encs/pkg/jdk-8/root/jre`<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`setenv CATALINA_BASE /nfs/groups/x/xyz_soen487_i/xyzTomcat`<br/>

You will need to add `manager`, `manager-gui` or `manager-script` roles to their respective `usernames` provided by you in the `CATALINA_BASE/conf/tomcat-users.xml`.<br/>

After adding a server instance of your desire you need to import `MashupTest` into `OpenESB` using the `Import >> EclipseProject >> Import Project ingnoring Project Dependencies` and then you can reconfigure to choose one of the server instances if required.<br/>
Once the project is deployed, run it on `http://localhost:8080/WEB-INF/test-src.html`<br/>
Press `F12` to bring up the browser console and you will see the output depicting urlfeeds from different urls into one mashup. 

### SinglePolicy
