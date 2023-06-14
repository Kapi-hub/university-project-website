# Shotmaniacs Booking System

Welcome to the repository of Shotmaniacs Group 1. This project is part of Module 4 of Technical Computer Science at UT in the year 2023.

## Table of Contents
- [About the Team](#about-the-team)
- [Project Description](#project-description)
- [Features](#features)
- [Screenshots](#screenshots)
- [Usage](#usage)
- [Install locally](#install-locally)
    - [Tomcat Installation](#tomcat-installation)
    - [Adding the Password Environment Variable for the Database](#adding-the-password-Environment-Variable-for-the-database)
    - [IDE Setup - IntelliJ IDEA](#ide-setup---intellij-idea)

## About the Team
We are a driven team aiming to build a great product while having fun and learning more about the technologies we are using. The students involved are:
- Beau Jonkhout, s2767619
- Raluca Gavrila, s2930277
- Alexandru Culda, s3022684
- Raphael Ritz, s3011313
- Baris Yilmaz, s2968525
- Pavan Awadhpersad, s2666499

## Project Description
The Shotmaniacs Booking System is being developed to replace the current booking system of Shotmaniacs, a company providing bars and events with photography and social media coverage. This web application aims to provide an improved and efficient booking experience for the customers, as well as a more streamlined process for the employees of Shotmaniacs.

## Features
<!-- We should add a list of supported feature here, or maybe something like a status at the end: "available", "coming soon" and "yet to come". -->
- Feature 1: [Description] 
- Feature 2: [Description]
- Feature 3: [Description]

## Screenshots
<!-- We should add some screenshots or GIFs of the system here -->
![Screenshot 1](path/to/screenshot1.png)
![Screenshot 2](path/to/screenshot2.png)

## Usage
<!-- We should add something like a tutorial of the webpage here, with a section for client, one for crew and one for admin, and subsections for each feature. -->
As of right now, a prototype of the webapp is available at [hosted-by-previder.com](http://shotmaniacs1.paas.hosted-by-previder.com/).</br>This prototype is not fully functional yet, but it should give you an idea of what the final product will look like.
- The crew & admin login form is available [here](http://shotmaniacs1.paas.hosted-by-previder.com/pages/login/index.html)
- The client submission form can be found [here](http://shotmaniacs1.paas.hosted-by-previder.com/pages/client/index.html).
> To test the prototype, you can use the following credentials:
> - Crew: `testCrew` / `testCrewPassword`
> - Admin: `testAdmin` / `testAdminPassword`

## Install locally

To install the webapp locally, you will need Tomcat and an IDE of your choice. We recommend using IntelliJ, but Eclipse or any other IDE should work as well.

### Tomcat Installation
If you don't have Tomcat installed already, follow these steps:
1. Download the latest version of Tomcat [here](https://tomcat.apache.org/). We are using Tomcat 10.1.9.
2. Extract the downloaded Tomcat zip file to your desired location.

### Adding the Password Environment Variable for the Database
The password for the database is, of course, not published on Git, and you should have access to it already.
<!-- TODO: Add instructions on how to get the password, how to configure database -->

To add the password variable:
1. Locate the previously installed Tomcat folder in your file explorer (e.g., `apache-tomcat-x.x.x`).
2. Open a terminal in this folder (usually by right-clicking and selecting the "Open in terminal" option).
3. Navigate to the bin folder:
    ```bash
    cd ./bin
    ```
4. Setting up the Database Password

   To configure the database password, follow the steps below based on your operating system.

   #### Windows

    1. Open a PowerShell terminal with administrator privileges:
        - Refer to this [tutorial](https://www.makeuseof.com/windows-11-powershell-administrator/) for detailed instructions on running PowerShell as an administrator.

    2. Set the environment variable by running the following command:
       ```bash
       [Environment]::SetEnvironmentVariable("DB_PASSWORD", "[password]", "Machine")
       [Environment]::SetEnvironmentVariable("MAIL_CLIENT_ID", "[mail_client_id]", "Machine")
       [Environment ]::SetEnvironmentVariable("MAIL_CLIENT_SECRET", "[mail_client_secret]", "Machine")
       
       ```
       Replace `[password]` with the actual password.
    3. Replace `[mail_client_id]` and `[mail_client_secret]` with the actual id and secret you can find in the JSON.

    3. Restart your system to apply the changes.

    4. Double-check that the changes have been applied by running the following command in PowerShell:
       ```bash
       Get-ChildItem Env:
       ```
       Verify that the output contains the `DB_PASSWORD` variable with the correct password.

   #### Linux

    1. Open a terminal.

    2. Set the environment variable by running the following command with administrative privileges:
       ```bash
       sudo echo "export DB_PASSWORD=[password]" >> /etc/environment
       sudo echo "export MAIL_CLIENT_ID=[mail_client_id]" >> /etc/environment
       sudo echo "export MAIL_CLIENT_SECRET=[mail_client_secret]" >> /etc/environment
       ```
       Replace `[password]` with the actual password.

    3. Restart your system to apply the changes.

    4. Double-check that the changes have been applied by running the following command in the terminal:
       ```bash
       source /etc/environment
       ```
       Verify that the output contains the `DB_PASSWORD` variable with the correct password.


### IDE Setup - IntelliJ IDEA
If you don't have IntelliJ IDEA installed yet, you can download and install the latest version from [here](https://www.jetbrains.com/idea/download/). We are using IntelliJ IDEA 2023.1.2.

To set up the project in IntelliJ IDEA:
1. Install two plugins: `Maven` and `Tomcat and TomEE`.
    - If you already have a previous project open, go to `File > Settings > Plugins`, otherwise, simply click `Plugins` from the startup screen.
    - Click `Marketplace` in the top bar.
    - Search for `Maven`, click `Install`, and then `OK`.
    - Repeat the previous step for `Tomcat and TomEE`.
    - Restart IntelliJ.
2. Clone the repository:
    - If you already have a previous project open, go to `File > New > Project from Version Control`.
    - If you're on the startup screen, select `Get from Version Control` from the startup screen.
    - Select `Git` and paste the URL of this repository (https://gitlab.utwente.nl/di22/shotmaniacs1.git) into the URL field.
    - Choose a location on your computer to store the project.
    - Click `Clone`.
3. Import the project:
    - When the project is loaded, IntelliJ should prompt you to import the project. Select `Import Maven project`.
4. Configure Tomcat server:
    - Go to `Run > Edit Configurations` and click the `+` button in the top left corner.
    - Select `Tomcat Server > Local`.
    - Give the configuration a name (e.g., `Tomcat Server`).
    - Click `Configure...` next to the `Application server` dropdown.
    - Add the directory of the previously installed Tomcat server as the Tomcat Home directory and click `OK`.
    - Go to the `Deployment` tab and click the `+` button in the top left corner.
    - Select `Artifact... > shotmaniacs1:war`.
    - Modify the `Application context` to a single forward slash `/`.
    - Click `OK` to save the configuration.
5. Run the project:
    - Click the green play button in the top right corner.
    - Your browser should open and show the main page of the application.

## Contribution Guidelines

### Adding a New Feature

To add a new feature to the webapp, follow these steps:
1. Create a new branch from the `main` branch with the name of the feature you are adding.
2. Implement the feature in the new branch.
3. If and only if it works flawlessly, merge main into your branch.
4. Once again, double-check that it indeed still works flawlessly.
5. Create a merge request from your branch to `main`.

### Fixing a Bug

#### Minor bugs
Minor, easy to fix bugs may be fixed directly on the `main` branch.

#### Major bugs
Major bugs should be fixed in a new branch, following the same steps as for adding a new feature [as shown above](#adding-a-new-feature).
If you are unsure about the severity of the bug, follow this approach to be safe.

### How to add a new web-page

#### HTML file
The html file to your new web-page should be added to the `src/main/webapp/WEB-INF/pages/` folder.
To make the page accessible, you need to add a new entry into the static constructor of the `PageMapping` class.
Here, you need to specify the url at which the page should be accessible, the location of the HTML file, and the permission required to access the page.
> **_Choosing a URL:_**
> The url should be chosen to be as compact as possible, while still being descriptive of the page's content.
> Ensure it fits in with the rest of the urls in the application. (i.e., use the same naming conventions, for example, use `admin` instead of `administrator`).

The location should be the relative location in the `/WEB-INF/pages/` directory.
The permission should be one of the permissions specified in the `AccountType` enum, or `null` if no permission is required to access the page.
(Please note that client accounts and therefore permissions are only supported in theory, and not yet implemented in the application.)

#### CSS and JS files
The CSS and JS files for your new web-page should be added to the `src/main/webapp/WEB-INF/publicResources/` folder.
To link the CSS and JS files to your HTML file, prepend the relative path inside of the `/publicResources/` directory with `/static/`
For example, if you have a CSS file located at `src/main/webapp/WEB-INF/publicResources/admin/myCssFile.css`, you should link it in your HTML file as follows:
```html
<link rel="stylesheet" href="/static/admin/myCssFile.css">
```
This may seem unnecessarily complicated, but is necessary to defend against path traversal attacks.

### How to add or modify Jersey resources

#### Java file
The Java file for your new Jersey resource should be added to the `src/main/java/resources/` folder.
In this file, ensure you explicitly specify which permission is required to access the resource by adding the `@RolesAllowed` annotation to the class.
As of right now, the only permissions that are supported are `admin` and `crew_member`, with a support fo `client` already present, but impossible to use as clients currently have no way to log in.
This can be done as follows:
```java
@RolesAllowed("admin")
```
or
```java
@RolesAllowed({"admin", "crew_member"})
```

If this resource is public, meaning it does not require any permissions, ensure to explicitly specify this by adding the `@PermitAll` annotation to the class.
```java
@PermitAll
```

To specify which HTTP method(s) are supported by the resource, add the `@GET`, `@POST`, `@PUT` or `@DELETE` annotation to the method.
```java
@GET
```

Ensure you specify which media type(s) are supported by the resource by adding the `@Consumes` annotation to the method.
```java
@Consumes(MediaType.APPLICATION_JSON)
```

To specify the path of the resource, add the `@Path` annotation to the class and method.
```java
@Path("/myResource")
```
> **_Note:_** The path of the resource is relative to the path of the application, which is `/api/` by default.
> For example, if you specify the path of the resource class as `/myResource` and the path of the resource method as `/myMethod`, the full path of the resource will be `/api/myResource/myMethod`.

Ensure the responsibilities of the resource are clear from the name of the class and method.
Decide on the resource's responsibilities in an intelligent way, for example, it makes much more sense to have a resource responsible for all announcements than to separate resources by user account type:
This will cause overlapping methods, as for example both admins and crew members should be able to view announcements, and will make it harder to maintain the codebase.
It is true that only admins are allowed to create announcements, but this type of permission checking should be done by the role annotations and not be reflected in the resource's name and logic.


#### DAOs
If your resource requires access to the database, you should create a new DAO class in the `src/main/java/dao/` folder.
This class should be a singleton. For consistency, use an enum with a single instance called `I` or `instance` to achieve this.4
In most cases, you will want to ensure that each DAO is only responsible for a single table in the database, as this keeps the design clean, modular, and easier to understand.
If you need to access multiple tables, create a new DAO for each table.
If the table you need to access already has a DAO, use that one instead of creating a new one.
Of course, there are cases where complex queries require access to multiple tables, but in most cases, this is not the case.
If your query is very complex, consider using views and/or stored procedures to simplify it.
Ensure DAOs are responsible only for interacting with the database, i.e., querying and updating the database. They should not contain any business logic.
To access the database, use a Connection object, which you can get by calling the `misc.connectionFactory.getConnection()` method.
This method will return a Connection object that is already connected to the database.
To execute queries, ensure to only use prepared statements, as this prevents SQL injection attacks.