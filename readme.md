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
       ```
       Replace `[password]` with the actual password.

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