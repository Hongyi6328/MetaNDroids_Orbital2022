# Parti.
Main repository for team MetaNDroids (5174), Apollo 11, Orbital 2022

# Introduction
## Summary
An Android-based app that allows users to participate in each other's projects.

## Motivation
It is pretty common that university students are required to do an individual/group project. It could be a software app, a survey, an experiment, etc. Whatever the project is, the fact is that participants **ARE NEEDED**, be they test users, surveyees, or experiment subjects! So often we see students posting their advertisements and invitations in random Telegram groups, on personal websites, or at bus stops, but very few really respond. As a result, a lot of Telegram groups, such as module discussion groups (the GEA1000 group is the worst) and interest groups, are full of spam. Personal websites end up having low views. Bulletin boards at bus stops are also messed up by an overwhelming number of posters. Other students are distracted and bothered, so they pay even less attention to such advertisements in the future. It is hard for initiators to find participants, and conversely, students who would like to participate in their peers’ works cannot find the projects which interest them. So what goes wrong? Why is it so hard for project developers to find other students to participate in their projects? We believe this is due to the following reasons:
1. The absence of **a platform that serves as a bridge or an agent between project developers and potential participants.** Information is not aggregated but instead, dispersed. Therefore, different project developers seek participants individually but do not have the opportunity to post their requests onto a shared public platform. Potential participants do not have enough information about what projects are ongoing and might interest them.
2. **The lack of a reward system** that really motivates people to participate.
3. Project developers only focus on their own projects, but **they can also be each other’s participants.** They are the ones who have the strongest incentive to participate in others’ projects, if other developers participate in theirs in return. However, there has yet to be a platform on which developers from different projects can collaborate.

Hence, we want to develop a solution to these three problems.

## Aims
* Build a **centralised platform** that serves as a bridge and an agent between project developers and potential participants. Information is to be aggregated and shared on it.
* Invite more users to use projects developed by students.
* Build a **mutually beneficial, collaborative** platform where project developers and researchers can participate in each other’s projects.
* Assist **research organisations and non-profit institutions** in recruiting volunteers for experiments/surveys/social work.
* Involve students in the process of **ranking/advertising** the projects of their peers, enabling them to support those projects that engage or benefit them.

## Scope
The target audience is all university / polytechnic students.
The platform **IS NOT**:
* A job market where people apply for jobs and internships
* A social networking platform
* A school forum where people talk about issues at university
	
The platform **DOES NOT**:
* Accept commercial entities to post their projects to seek more users for commercial / marketing purposes
* Tell the project developers how to improve their projects, such as debugging. They should make sure their projects work before they invite participants.

## User Stories 
The core of the mechanism is called the **Participation Point** system.
* As a project developer, I can use my Participation Points to **boost the ranking** of my project, so that more users on the platform can see it and participate in it.
* As a project developer, I can actively participate in other developers’ projects and **rank** a project and **provide feedback on it**, so that I can earn more Participation Points to support my project.
* As an ordinary student, I can earn Participation Points and **transfer** it to my friends, so that they can user it for their projects.
* As an ordinary student, I also **give constructive and critical feedback** and **donate** my Participation Points, so that I can endorse my favourite projects.

# Get Started
The application is a mobile app on the Android system, so to test it or use it, you need an Android environment. Please select one of the following methods to run the app, according to the device you have.

## Run the App on a Physical Android Smartphone
If you have a **physical** Android device, you may run the app on it.
* The Android SDK (Software Development Kit) version on your smartphone shall be at least **26**; otherwise, the application cannot run on it, and you have to choose another method to run the application. You can check the version by following the steps provided on this [website](https://its.uiowa.edu/support/article/2803).
* The recommended SDK version is **32**.
* The recommended screen size is **5** inches. You can check your screen size following the instructions [here](https://www.samsung.com/uk/mobile-phone-buying-guide/what-screen-size/).

It is okay if your smartphone does not have the recommended version or screen size. The application should be able to run, but potentially with some **distorted or not-to-scale user interface.** 

* Now if you are sure that your smartphone can run the application, you may proceed to download the .apk file from [Google Drive](https://drive.google.com/file/d/1SV0ERNrYLtcERg7zPqiyn7yMf4wMekgh/view?usp=sharing) or [Github](https://github.com/Hongyi6328/MetaNDroids_Orbital2022/blob/main/Parti.apk). As soon as you download it successfully, the Android system will automatically notify you to **install** the downloaded .apk file. In case you miss the notification or the system does not notify you, you can still install it manually.
**Open your file manager (the ‘Files’ application) and go to the default download location of your system.** If you are not sure where it is, do check out this [link](https://www.androidpolice.com/how-to-find-downloads-on-your-android-phone/#:~:text=To%20quickly%20find%20where%20your,files%20in%20that%20specific%20folder).
* Click on Parti.apk and wait for the installation to complete.

If the installation is successful, Parti will appear in your main application menu with the logo ![logo](docs/app_logo.png), and you are ready to run it. Please read the next section on Using the App and Features.

## Run the App on Android Studio
If you do not have an Android smartphone or your smartphone does not meet the system requirement, you can also run the app on Android Studio.
If you do not have Android Studio installed on your PC, please download it here. Android Studio is compatible with Windows, MacBook, Linux and Chrome OS.
Open Android Studio and click on the ‘Device Manager’ tab highlighted in yellow, which provides a variety of Virtual Android Machines.
Click on ‘Create device’.
Choose Pixel 2 (preferably) or any hardware device you want.
Choose API Level at least 30.
Select ‘Portrait’ as the default orientation and click on ‘Finish’. Android Studio will take some time to create the device. Android Studio, along with the virtual device, may occupy a lot of memory space on your PC, so please remember to remove it after testing the app. 
While you are waiting for the device to get ready, download the .apk file from Google Drive or GitHub from Github. It should be saved at the default download location on your PC, usually C:\user\downloads for Windows and Files/downloads for MacBook.
After the device is created, simply drag the Parti-debug.apk file to the screen of the device. The system will automatically install the file for you. If it does not, drag the file again, check the notification centre and the event log at the bottom right corner.
Upon successful installation, click on the dot at the bottom menu, hold it, and move it up to raise the application menu. 
Open .
Now you are ready to run it. Please read the next section on Using the App and Features.
Run the App on an Online Android Emulator
You might not wish to download a big bundle of files solely to run a light app. An online Android emulator can be an alternative. You can use a variety of online emulators on the internet, but bear in mind that many of them are not safe and may contain inappropriate information, such as scams. We do not recommend you to use online emulators, but you are definitely free to do so. The procedures are basically the same as what you do to run the app on an Android Studio emulator.
















# Project Structure

## Use Case Diagram
![Use Case Diagram](docs/use_case_diagram.png)

## Architecture Diagram
![Architecture Diagram](docs/architecture_diagram.png)

## Data Dictionary

### User
| Field | Data Type | Size | Remark |
| ----- | --------- | ---- | ------ |
| uuid | String | 28 characters | The primary key. The ID that uniquely defines a user and never changes after registration.
| email | String |  | For authentication and security purposes.
| alias | String | Up to 20 characters | User defined alias/nickname, which can be modified for as many times as the user.
| profileImageId | String | 28 characters | The ID of profile image saved in Firebase Storage.
| participationPoints | double | 8 bytes | Earn PPs by participating in others' projects. Spend PPs by donating or calling for participants.
| projectPosted | List\<String\> | 28 characters each entry | The ID of projects posted by this user.
| projectsParticipated | List\<String\> | 28 characters each entry | The ID of projects this user participated in.
| major | Enum |  | The major this user is in.
| yearOfMatric | String | 4 characters | Year of matriculation.
| selfDescription | String | Up to 5000 characters | A short paragraph of to introduce youself.
| commentsPosted | List\<String\> | 28 characters | The ID of projects commented by this user.
| participationPointsEarned | Map\<String, Double\> | \<28 characters, 8 bytes\> | A map that keeps track of PPs earned from projects respectively.

### Project
| Field | Data Type | Size | Remark |
| ----- | --------- | ---- | ------ |
| projectId | String | 28 characters | The primary key. The ID that uniquely defines a project and never changes after posting.
| name | String | Up to 100 characters | User-defined title, can be changed many times.
| projectType | Enum | | Software application, survey, experiment and other.
| concluded | boolean | 1 bit | Whether participants are still needed.
| admin | String | 28 characters | The poster of this project, which is the only person that can add/remove a developer.
| developers | List\<String\> | 28 characters each entry | Collaborators of the project.
| participants | List\<String\> | 28 characters each entry | Participants of the project.
| numActions | int | 4 bytes | The number of actions done for this project.
| numActionsNeeded | int | 4 bytes | The expected number of actions done.
| numParticipants | int | 4 bytes | The number of participants in this project so far.
| numParticipantsNeeded | int | 4 bytes | The expected number of participants.
| ranking | double | 8 bytes | The ranking of this project, used to sort projects.
| dynamicRanking | double | 8 bytes | The dynamic component of ranking.
| staticRanking | double | 8 bytes | The static component of ranking.
| description | String | Unlimited | A short paragraph that describes what the project is about and how to participate.
| comments | List of String | 28 characters each entry | The ID of comment posters.
| numComments | int | 4 bytes | The number of comments so far.
| totalRating | long | 8 bytes | The total rating of comments.
| lastUpdateDate | String | 24 characters | The latest timestamp when the ranking is updated, using the pattern "yyyy-MM-dd HH:mm:ssZ" with [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html).
| imageId | String | 28 characters | The ID of project image saved in Firebase Storage.
| participationPoints | double | 8 bytes | The amount of PPs that will be given to prospective participants.
| participationPointsBalance | double | 8 bytes | The amount of PPs deposited in the project (unspent PPs).
| donatedParticipationPoints | double | 8 bytes | The cumulated amount of PPs donated by users, used to calculate the ranking.
| donors | Map\<String, Double\> | 8 bytes each entry | A breakdown of donors and their respective amount of donated PPs.

### Comment
| Field | Data Type | Size | Remark |
| ----- | --------- | ---- | ------ |
| senderId | String | 28 characters | The ID of the commenter.
| comment | String | 2000 characters | The body of the comment.
| rating | int | 4 bytes | From 0 to 5 inclusive, a higher number means more postive feedback.
| lastUpdateDate | String | 24 characters | The latest timestamp when the comment is updated, using the pattern "yyyy-MM-dd HH:mm:ssZ" with [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html). Used to calculate ranking.

### Verification Code
| Field | Data Type | Size | Remark |
| ----- | --------- | ---- | ------ |
| code | String | 28 characters | The actual code, no duplicates.
| participant | String | 28 characters | The ID of the user who redeemed the code.
| participationPoints | double | 8 bytes | The amount of PPs this code is worth.
| redeemed | boolean | 1 bit | True **iff** the code if redeemed already.
| redeemable | boolean | 1 bit | True **iff** the code is **NOT** redeemed **AND** the code is available.

### Verification Code Bundle
| Field | Data Type | Size | Remark |
| ----- | --------- | ---- | ------ |
| projectId | String | 28 characters | The project associated with this bundle.
| numRedeemed | int | 4 bytes | The number of redeemed codes in this bundle.
| numRedeemable | int | 4 bytes | The number of codes that can be redeemed now.
| verificationCodeList | List of Verification Code |  | A list of codes associated with the project.

## Tech Stacks
* Java
Java is the fundamental programming language in which Android is built. Being familiar with Java is essential to start the project.
* Android Studio and Android SDK
Android Studio is the official IDE designated for Android development. The main part of the work will be done in Android Studio for Windows. Android SDK is a collection of original libraries, configurations, documentation and tutorials that will aid us in getting acquainted with Android development quickly. The built-in libraries are also effective and efficient for us to complete common tasks, such as basic security configuration, running environment setup, and device compatibility check. Java files written with Android Studio and Android SDK will control the UI logic. 
* AndroidX UI
A library with a wide variety of ready-to-use frontend UI components, such as icons, buttons, and forms. It will be used for interface design.
* Firebase Firestore and Firebase Storage
Firebase is a platform developed by Google for creating mobile and web applications. It is now their flagship offering for app development. It serves as a remote data storage and BaaS (Backend-as-a-Service) server, using which does not require too much coding at the backend. Firestore stores plain data, such as string, number, and boolean, while Storage stores files, such as images.
* Firebase Android SDK and BoM (Bill of Materials)
Firebase Android SDK provides a lot of Android plug-in libraries that facilitate database-related operations, such as asynchronous data retrieval, which will be discussed later in the report. These operations are hard to implement without the support of Firebase Android SDK. The Firebase Android BoM (Bill of Materials) enables the developer to manage all Firebase library versions by specifying only one version — the BoM's version. When the developer uses the Firebase BoM in the app, the BoM automatically pulls in the individual library versions mapped to BoM's version. All the individual library versions will be compatible.
