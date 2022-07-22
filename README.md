# Parti.
Main repository for team MetaNDroids (5174), Apollo 11, Orbital 2022
# Introduction
## Summary
**Parti.**, an Android-based app that allows users to participate in each other's projects.
## Motivation
It is pretty common that university students are required to do an individual/group project. It could be a software app, a survey, an experiment, etc. Whatever the project is, the fact is that participants **ARE NEEDED**, be they test users, surveyees, or experiment subjects! 

So often we see students posting their advertisements and invitations in random Telegram groups, on personal websites, or at bus stops, but very few really respond. As a result, a lot of Telegram groups, such as module discussion groups (the GEA1000 group is the worst) and interest groups, are full of spam. Personal websites end up having low views. Bulletin boards at bus stops are also messed up by an overwhelming number of posters. Other students are distracted and bothered, so they pay even less attention to such advertisements in the future. It is hard for initiators to find participants, and conversely, students who would like to participate in their peers’ works cannot find the projects which interest them. 

So what goes wrong? Why is it so hard for project developers to find other students to participate in their projects? We believe this is due to the following reasons:
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
* **The recommended SDK version is 32**.
* **The recommended screen size is 5 inches.** You can check your screen size following the instructions [here](https://www.samsung.com/uk/mobile-phone-buying-guide/what-screen-size/).

It is okay if your smartphone does not have the recommended version or screen size. The application should be able to run, but potentially with some **distorted or not-to-scale user interface.** 

* Now if you are sure that your smartphone can run the application, you may proceed to download the .apk file from [Google Drive](https://drive.google.com/file/d/1SV0ERNrYLtcERg7zPqiyn7yMf4wMekgh/view?usp=sharing) or [Github](https://github.com/Hongyi6328/MetaNDroids_Orbital2022/blob/main/Parti.apk). As soon as you download it successfully, the Android system will automatically notify you to **install** the downloaded .apk file. In case you miss the notification or the system does not notify you, you can still install it manually.
* Open your file manager (the ‘Files’ application) and go to the **default download location of your system.** If you are not sure where it is, do check out this [link](https://www.androidpolice.com/how-to-find-downloads-on-your-android-phone/#:~:text=To%20quickly%20find%20where%20your,files%20in%20that%20specific%20folder).
* Click on Parti.apk and wait for the installation to complete.

If the installation is successful, Parti will appear in your main application menu with the logo <img src="docs/app_logo.png" height="50" width="50">, and you are ready to run it. Please read the next section on Using the App and Features.

## Run the App on Android Studio
If you do not have an Android smartphone or your smartphone does not meet the system requirement, you can also run the app on Android Studio.
* If you do not have **Android Studio** installed on your PC, please download it [here](https://developer.android.com/studio?gclid=CjwKCAjw5NqVBhAjEiwAeCa97XMhMTqyrDV9YNK6dB9iltn3hcKoH-V2i2pSwFSSBJMkxC1jpfOVLRoCJHcQAvD_BwE&gclsrc=aw.ds). Android Studio is compatible with **Windows, MacBook, Linux and Chrome OS.**

* Open Android Studio and click on the **‘Device Manager’** tab highlighted in yellow, which provides a variety of **Virtual Android Machines.** 

	<img src="docs/get_started/get_started_1.png">

* Click on **‘Create device’.**

	<img src="docs/get_started/get_started_2.png">

* Choose **Pixel 2** (preferably) or any hardware device you want.

	<img src="docs/get_started/get_started_3.png">

* Choose **API Level at least 30.**

	<img src="docs/get_started/get_started_4.png">

* Select **‘Portrait’** as the default orientation and click on **‘Finish’**. Android Studio will take some time to create the device. Android Studio, along with the virtual device, may occupy a lot of memory space on your PC, so please remember to remove it after testing the app.

* While you are waiting for the device to get ready, download the .apk file from [Google Drive](https://drive.google.com/file/d/1SV0ERNrYLtcERg7zPqiyn7yMf4wMekgh/view?usp=sharing) or [Github](https://github.com/Hongyi6328/MetaNDroids_Orbital2022/blob/main/Parti.apk). It should be saved at the **default download location** on your PC, usually C:\user\downloads for Windows and Files/downloads for MacBook.

* After the device is created, simply **drag the Parti.apk** file to the screen of the device. The system will automatically install the file for you. If it does not, drag the file again, check the notification centre and the event log at the bottom right corner.

	<img src="docs/get_started/get_started_5.png">

* Upon successful installation, click on the **dot at the bottom menu**, hold it, and move it up to raise the application menu. 

	<img src="docs/get_started/get_started_6.png">

* Open <img src="docs/app_logo.png" height="50" width="50">.

Now you are ready to run it. Please read the next section on Using the App and Features.

## Run the App on an Online Android Emulator
You might not wish to download a big bundle of files solely to run a light app. An online Android emulator can be an alternative. You can use a variety of online emulators on the internet, but bear in mind that many of them are not safe and may contain inappropriate information, such as scams. We **do not** recommend you to use online emulators, but you are definitely free to do so. The procedures are basically the same as what you do to run the app on an Android Studio emulator.

# Basic Features
## Sign-up
The first page the app shows is the log-in page. If you do not have an account yet, kindly click on the 'Sign Up' button at the bottom left.

[comment]: <> (Image)

You will be redirected to the sign-up page. 

Enter your **REAL** email address and preferred password here. Please note that your password has to be at least **8 characters long with at least 1 uppercase letter, 1 special character, and 1 digit**, for account security. Also, make sure that the passwords you enter **match**. If any of the above two conditions are not met, you will fail to sign up and have to do it again.

The system will pop up a message  if you successfully create an account. Please remember your registered email and password. Now, the last thing to do before your account is ready is to verify your email. Go to your mailbox and **check your junk mail box if needed**, click on the verification link sent by the system to verify your account. During the process, please **do not navigate back to the log-in page**; otherwise you will need to go through the whole process from the start again. 

After you verify your email address, click on the "Confirm Verification" button, and you will be directed to the main page. If you do not receive a verification email, you can click on the "Resend Verification Email" button.

Since the development phase has concluded, your account will not be removed. Be sure to use our platform in the future.

## Log-in
You may already have an account in hand, and what you need to do is **sign in** to your account. 

[comment]: <> (Image)

On the first page, below the app logo, you can enter your account credentials to sing in. If you key in an email address that is not present in the database or a wrong password by mistake, you will get a message . Please double check your entries. If you forget your password, click on the **"Reset Password"** button at the bottom right and get **an email to reset it.** Successful log-in will render you another message .

**After some time of inactivity, you will be signed out.**

## Log-out
Upon successful log-in, if you have saved all changes and do not wish to stay logged in, you can log your account out by navigating to the **"My Profile"** tab, scrolling down to the very bottom, and clicking on the **"Log Out"** button. After doing so, another log-in operation is required to get back the access to your account.

## Navigate Between Views (Fragments)
In Android systems, a page that is designated for a particular purpose and integrated into the main container is called a **fragment.** There is a navigation bar at the bottom, which contains 3 items: “Browse Projects”, “Browse Users", and “My Profile”. You can navigate between them simply by clicking on the corresponding item. More about the 3 fragments will be discussed later.

## Browse Projects
The "Browse Projects" fragment is for you to browse projects.

[comment]: <> (Image)

The main data container is called a **Recycler View**, in which every item represents a project. As shown in the screenshot, each list item consists of the title, a short description, the average rating (from 0 to 5 inclusive), a horizontal bar indicating the progress, and a project image. You can scroll the list up and down.

There is a dropdown menu at the top of the view, in which you can **filter the projects** by some conditions. You may select the projects you posted only, or projects that you have participated in.

Not all information about the project is shown because the purpose of this fragment is only to provide you with brief outlines about projects. To take a deep look into a project, simply click on it and you will be navigated to another view.

## View Project Details
After clicking on a project in the "Browse Projects" fragment, an activity pops up and shows you **full details about that project**. You can also freely scroll the view up and down.

[comment]: <> (Image)

The details include its image with bigger size, its type (software app, survey, experiment or other), the full version of project description, and a rating bar displaying .

### Verification Codes
There is a section for your to enter verification code. **Verification code is used to confirm that a user really participated in a certain project.** A single "participation" is called an **action**. A user can take multiple **actions** for a project, each rewarding the user some **Participation Points**. For example, user A posts a software app on **Parti.**, and the system will send an email along with a list of available verification codes to A.  User B participates in A's project, so A gives one of the codes to B. The latter enters the code on the platform, so that the platform knows user B really participates and gives B the corresponding amount of **Participation Points.** Furthermore, A can invite B to participate in the project again if additional assistance needed. After that, B can enter another code to redeem the reward.

**You cannot enter verification code for your own projects.**

More on verification code will be discussed later here. [comment]: <> (link)

### Donations
You may donate PPs to a project to boost its ranking (dynamic and staic, which will be discussed later) [comment]: <> (link). PPs will be deducted from your account balance, but it will not be transferred to the project host directly, because it is for the project in particular. In this sense, your donated PPs kind of "comes to void", however boosting the ranking as a gesture of support. The more you donate, the higher resulting ranking.

**You can donate PPs to your own projects.** Just make sure you have a **sufficient** amount.
 
Click on the ‘Back’ button at the top left to return to the main page.

### Comments
You can browse other participants' comments to get a bit of sense about a project, and decide on whether you want to participate or not. After you participate in a project, i.e., you have submitted at least one verification code, you can leave a comment on the project, talking about experience, further improvements etc. Note that rating matters in ranking the project. **We welcome constructive and critical feedback. Please be kind to others.**

For the sake of simplicity, a participant is allowed to leave one comment per project. If you take multiple actions, you can update your comment and rating later. Your previous comment will be pre-loaded for the ease of modification.

**You can also delete your comment. Whenever you have something to say, add a new one.**

## Create Your First Project
To create a project that belongs to you, click on the **"New Project"** button in the "Browse Projects" fragment. The view shown below will pop up.

[comment]: <> (Image)

**You can set the project image by simply clicking on the default info icon at the top.** You may also want to give a good name to your new project, and talk about what it is about and how to participate in the project description section. Finally, do not forget to set the number of actions you need and the reward for each action. The latter will affect the ranking of your project. **Higher Participation Point reward will definitely grab more people's attention.**

Make sure that you have sufficient PP balance to launch the project. Once the project is created, PPs for it will be deducted from your account and saved in a temporary deposit first, which is called the **"project balance"**. After a user redeems a verification code, the corresponding amount of PPs will be transferred from the balance to the user. This mechanism will be discussed later here [comment]: <> (link).

Click on the **"Submit"** button at the top right to submit this project. Upon successful submission, you will receive an email along with **a list of valid verification codes** for the project. **Manage them properly, and give them to your participants at the right time** (once you think that they completed an action). If you did not receive that email, click on the "Submit" button again. 

## Edit Your Projects
There is a need to manage and edit the projects you posted, too! In the **"View Project"** view, if you are the host of the project, the **"Edit"** button will be visible, hovering at the top right. Click on that to modify everything as if you are creating a new project, just with one slight difference -- **The number of actions needed cannot be smaller than the actual number of actions done.**

Remember the concept of **project balance** mentioned in the above section? This is helpful when we have to refund some PPs to you after editing. More on that later. [comment]: <> (link). If you deposited more PPs previously than you need now, the additional part will be refunded to you immediately. Conversely, if the amount in the project balance is not enough, you need to pay more PPs now. So again, make sure you have sufficient amount.

If unfortunately, not enough actions have been taken and you have been waiting for so long, you can **get full refund by setting the number of actions needed to be equal to the actual number.**

## View and Update Account Details
In the **"My Profile"** fragment, you can view and update your account details, such as alias, year of matriculation and your major.

From the top to the bottom, the elements shown are your profile image, email, user ID and Participation Points (PPs). You can upload your custom profile image simply by clicking on it, and then, there will be a dialog for you to pick a photo from local storage. However, **you cannot change your registered email (at least for now), user ID and amount of PPs.** User ID cannot be changed because it is the key that uniquely defines a user account. Scroll down, and you can see more editable fields.

You can change and update them by clicking on the ‘Update’ button. Please note that some validation rules will be applied, such as no whitespaces in alias and length of self description no longer than 5000 characters. Data that do not pass validation will not be updated and you will be given respective instructions on how you can fix it.

## Search and View Other Users
You can explore what other users are doing and their interests. 
Click on the **"Browse Users"** tab in the main page, and you will see another RecyclerView, displaying all users. 

### Search
You may use the search bar at the top to search for a user by their alias. Note that the search results will **refresh every 0.75 seconds while you key in the input.** The system records timestamp of the last input event and compares it with the next input event. If the interval is greater than 0.75 seconds, then update the search results. Doing so makes it convenient to search, while ensuring the UI not jerky due to frequent refreshing.

### View a User
Click on a RecyclerView list item to take a closer look at a user. You can view their account details (of course you cannot change them), projects they posted, and projects they participated in.

### Transfer Participation Points
If you do not need participants at all, what is the point of using **Parti.**? Why earn so much PPs but not use it at all? The answer is that you can transfer PPs to your friends who need it! However, to make sure this feature is not abused, and to encourage users to earn PPs on their own, **a transfer rate of 0.9 applies.** As usual, make sure you have sufficient amount. **Upon successful transfer, the recipient will be notified via email.**

# Advanced Features
## The Ranking Algorithm
### Introduction
A ranking algorithm is used to evaluate the ranking of a project, which determines the order in which all projects are displayed. **A good ranking algorithm should seek a balance between popularity and time.** On one hand, projects that come later should have higher ranking than those that come earlier do; on the other hand, earlier projects might be more popular than later projects, and hence, have a higher ranking. A quandry is that new projects are fresh, not visited by too many people, so the sample size is too small for us to know its real popularity. Anyways, no matter how  a new project eventually turns out to be, we must give it a "temporarily exceptionally high" ranking and expose it to enough users first.

Among a bunch of prevailing ranking algorithms, we chose a simple [**"Vote and Decay"**](https://datagenetics.com/blog/october32018/index.html) algorithm, which fortunately, has good control of popularity and time. The key idea is that all user input, including participation, PP donation, and comment, is a "vote" to the project. You may  simply think a vote as **an instant boost of ranking.** However, such a boost will **decay over time and eventually take no effects.** A new project will be given **an intial vote to push it to a reasonably high place** in the project list. Then, its fate - remaining at the top or falling down all the way to the bottom - will totally depend on its own performance. 

For example, a new project is out, pushed to the top of list. However, not too many people are interested in it at the time, so it receives few votes. In a couple of days, it smoothly slips down to the bottom as its initial vote decays. Surprisingly, one day, a generous user donates an awful lot of Participation Points to it, which raises it to the top again. This time, many people see it,  participate in it, and leave positive comments, so it keeps being voted up, and the votes overcome the decay. As such, it remains at the top, and becomes one of the most popular projects for a long time.

As you can see in the above case, the "Vote and Decay" algorithm does a good job in taking care of popularity and time together.

### Mathematical Model
The key idea works, but it is still an idea. The implementation matters. There are many decay models, too. A classic yet simple model is called **Newton's Cooling**, or **Radioactive Decay.** The key idea is that the amount of decay at any moment is proportional to the remaining amount, which, in mathematical language, is written as

[comment]: <> (Image)
[comment]: <> (Image)

Where  _λ_ is a coefficient, _N_ the amount of vote, and _t_ the **temporal difference between the last update of decay and now.** After each evaluation, we also update the timestamp to be now.

This model elegantly solves a problem: since there are many votes, it is troublesome to calculate the decayed value of each vote and sum them up. A nice property of the model is that **the sum of decay is just the decay of sum**, so we can keep track of only one value. The model is time-context-independent, which means we do not care whether the amount was 200 recorded yesterday or 400 the day before. As long as the current amount is 100, we know it is going to be 50 tomorrow.

Upon a new vote, we update the current ranking and then, simply add the value of the vote to the total ranking. The new vote will decay with the remaining sum of previous votes **in the same rate.**

Another problem is the choice of  _λ_, which controls the speed in which ranking decays. It cannot be too fast; otherwise all projects will end up having a ranking around zero very soon. It cannot be too slow either; otherwise the relative trend between projects is not obvious. Either case will render projects not comparable. The choice of _λ_ has something to do with the typical recruitment period of a project. After some testing, we found that 10 days would be a good choice. **We expect a new project to decay to 10% of its initial vote after 10 days if there are no subsequent votes.** Let us talk about time in minutes. Then, 

[comment]: <> (Image)

### Variation
Everything seems good so far. Nonetheless, we still found the algorithm not very ideal, because most projects will end eventually, and at that time, they all have their rankings decay to around zero. **How can we distinguish projects that used to be popular from others?** Users may want to review past projects, but they will probably find all past projects jumbled at the bottom of list. Ordering by ranking does not work in this case.

Therefore, we proposed our own variation of the algorithm. The actual ranking of a projects is now **the sum of two components: one dynamic, and the other static.** The dynamic component decays over time, but the static component does not. A vote also has such two components. For a vote, **the dynamic part is typically 10 times the static part such that within a short time after the vote, the dynamic part dominates, but after a critical point, the static part dominates.**

With this variation, we can order old projects by their static ranking even after their dynamic ranking has already decayed to zero.

### Implementation
[comment]: <> (Image)

```
public static final double ACTION_DYNAMIC_VOTE = 100;  
public static final double ACTION_STATIC_VOTE = 10;  
public static final double COMMENT_DYNAMIC_VOTE = 150; //a comment is worth a higher vote
public static final double COMMENT_STATIC_VOTE = 15;  
public static final double DONATION_DYNAMIC_VOTE = 10;  
public static final double DONATION_STATIC_VOTE = 1;  
public static final double LAMBDA = 0.2303 / 60 / 24;

//Determine the vote by rating given, low rating give a negative vote.
public static double commentDynamicVote(int rating) {  
    return COMMENT_DYNAMIC_VOTE * (rating - 2);  
}  
  
public static double commentStaticVote(int rating) {  
    return COMMENT_STATIC_VOTE * (rating - 2);  
}  

//Determine the vote by the amount of donated PPs
public static double donationDynamicVote(double amount) {  
    return DONATION_DYNAMIC_VOTE * amount;  
}  
  
public static double donationStaticVote(double amount) {  
    return DONATION_STATIC_VOTE * amount;  
}

//The main logic to calculate the ranking
private void calculateRanking(double dynamicVote, double staticVote) {  
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime previous = ZonedDateTime.parse(lastUpdateDate,Parti.STANDARD_DATE_TIME_FORMAT); //The timestamp of the last update of ranking
    dynamicRanking = decay(dynamicRanking, previous, now) + dynamicVote; //The new dynamic ranking equals the decayed previous dynamic ranking plus the new vote
    staticRanking += staticVote;
    ranking = dynamicRanking + staticRanking;
    setLastUpdateDate(now.format(Parti.STANDARD_DATE_TIME_FORMAT));
}  
  
private double decay(double amount, ZonedDateTime earlier, ZonedDateTime later) {
	long diff = ChronoUnit.MINUTES.between(earlier, later); //The chronological difference between two timestamps
	return amount * Math.exp(-LAMBDA * diff); //The decay function
}
```

## The Verification Code and Participation Point System
Verification code

## Email Server












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
* **Java**

	Java is the **fundamental programming language** in which Android is built. Being familiar with Java is essential to start the project.

* **Android Studio and Android SDK**

	Android Studio is the **official IDE** for Android development. The main part of the work will be done in Android Studio for Windows. Android SDK is a collection of **original libraries, configurations, documentation and tutorials** that will aid us in getting acquainted with Android development quickly. The built-in libraries are also effective and efficient for us to complete common tasks, such as basic security configuration, running environment setup, and device compatibility check. **Java files written with Android Studio and Android SDK control the UI logic.**

* **AndroidX UI**

	A library with a wide variety of ready-to-use frontend UI components, such as icons, buttons, and forms. **It is used for interface design.**

* **Firebase Firestore and Firebase Storage**

	Firebase is a platform developed by Google for creating mobile and web applications. It is now their flagship offering for app development. It serves as a remote data storage and BaaS (Backend-as-a-Service) server, using which does not require too much coding at the backend. **Firestore stores plain data**, such as string, number, and boolean, while **Storage stores files**, such as images.

* **Firebase Android SDK and BoM (Bill of Materials)**

	Firebase Android SDK provides a lot of Android **plug-in libraries that facilitate database-related operations**, such as asynchronous data retrieval, which will be discussed later in the report. These operations are hard to implement without the support of Firebase Android SDK. The Firebase Android BoM (Bill of Materials) enables the developer to **manage all Firebase library versions by specifying only one version** — the BoM's version. When the developer uses the Firebase BoM in the app, the BoM automatically pulls in the individual library versions mapped to BoM's version. All the individual library versions will be compatible.

* **Firebase Trigger Email Extension and SendGrid**

	**Trigger Email** is a **Firebase Extension** that listens to new documents in a designated collection, converts them to emails, and direct them to an email server via **SMTP (Simple Mail Transfer Protocol)**. SendGrid (also known as Twilio SendGrid) is a Denver, Colorado-based customer communication platform for transactional and marketing email. It serves as an email distributor and monitor, by using which developers can **update users upon account changes via email** automatically.
