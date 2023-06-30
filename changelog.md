# Shotmaniacs 1 -- Changelog
## Sprint 1
#### Planning & UML
+ Created a Story Map.
+ Made a Cost Estimation
+ Made a Use-Case diagram
+ Made a Class Diagram
+ Created an SQL Schema
#### Mock-up
+ Created a client page in HTML
+ Created a crew dashboardd page in HTML
+ Created a calendar page in HTML
+ Created a login in HTML
+ Created a design of the dashboard in a drawing environment.
## Sprint 2
#### General
+ Set up back-end structure for client
    + This included models, misc, a dao & resources
    + This included patterns like Factory, DAO & Singleton.
    + Made use of the tomcat framework to have features on start up for instance.
+ Updated front-end pages of all client pages.
+ Security includes prepared statements for now.
+ The testing includes Postman, JUnit and general test-driven development.
#### Features
+ Single file uploading
+ Prototype of the calendar and dashboard
+
## Sprint 3
#### General
+ Back-end now has multi-level authentication.
    + Roles are separated in three categories: Crew, Admin & Client.
    + This also included clean page mapping to prevent path traversal.
+ Front-end now has a more similar layout to the rest of the pages.
#### Features
+ Client now has full functionality.
    + Including multiple event uploading and downloading of the excel template.
    + Including a mail confirmation.
+ Admins can now post announcements.
+ Staff can now authenticate through a login form.
## Sprint 4
#### General
+ Many finishing touches to the structure.
+ Redesign of the admin side.
+ Security now includes password hashing.
+ Testing also includes the use of sonarQube now.
#### Features
+ Crew can now enroll and un-enroll in the calendar dashboard.
+ Admins can create new members. 
+ Admins can manually add events.
+ Admins can view all events and crew members.
+ Admins can change crew members' roles and teams.
+ Admins can delete events.
