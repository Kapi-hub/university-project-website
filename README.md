
# Shotmaniacs 1

Welcome to shotmaniacs 1. A project for module 4 in the year 2023.


## About the team
We are a driven team trying to learn more about web programming.

The students involved are

* Beau Jonkhout, s2767619
* Raluca Gavrila, s2930277
* Alexandru Culda, s3022684
* Raphael Ritz, s3011313
* Baris Yilmaz, s2968525
* Pavan Awadhpersad, s2666499

## Installation

To get everything installed, you need to do the following things.

### Tomcat
Make sure to have tomcat installed. If not, you can download it here: https://tomcat.apache.org/

Please find out where you have installed the tomcat configuration.
It should be a folder that looks like apache-tomcat-x.x.x
#### Manually
1. Locate the tomcat folder. It has name like this `apache-tomcat-x.x.x `
1. Go to the /bin folder of the folder.
1. Add the file `di-2023-project-password`
1. Edit the newly added file: Add a single line, with the password in there.
* The password is not published on the git, but it should be known by you.

#### Command Line
You can also use cmd to do the following. Open CMD and enter the following commands.
1. The first line should be the apache-tomcat folder, this should be known to you. Locate it bin folder.
2. Afterwards, change the `[password]` block by the known password.


```cmd
cd C:\Development\apache-tomcat-10.1.8\bin
echo [password] > di-2023-project-password
```

Finally, you should have a file containing a secret password in the bin folder of the apache-tomcat folder.

### IntelliJ
1. Within intellJ, you can clone the directory from the GIT.
1. Please go to Project structure -> Dependencics -> + > JARs or directories -> Add the library `postgresql-42.3.4.jar` within the /lib folder in the project. Press apply.
1. Next, we update the artifact. Go to the Project structure -> Artifacts -> `shotmanics:war`
1. Under available elements, under shotmaniacs, the added postgresql.jar file should appear. Double click it to add it to the WEB-inf lib.
1. Press apply and then OK.
1. We are now adding a tomcat configuration. Go to the top right and add a tomcat configuration. This should preferably be Tomcat 10.1.8 (local).
1. Then go to the deployment and add the `shotmanaics:war` artifact. You can optionally change the application context to what you desire.
1. Now you should be able to start the tomcat configuration and it should automatically open up a browser tab where you can browse. 