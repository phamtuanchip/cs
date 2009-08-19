==============================================
    Release Notes - exo-cs - Version 1.3 RC1
==============================================

===============
 Introduction
===============

** eXo Collaboration Suite provides a rich, Web 2.0 browser-based interface with comprehensive
messaging, shared calendars, address books,  indexing, archival and search capabilities.

*eXo Address Book manages private and public contacts in your organization. A contact
lets you keep precious information such as personal or professional address and other
reaching information such as telephone, fax, email or IM. We support VCard format
import/export so that you can consolidate your existing contacts into a single place.
Additionally, you can refer your contacts from other applications such as eXo Calendar, eXo Mail.

*eXo Calendar helps you to organize and order time in your life more easily and efficiently. It
also manages your shared and public agendas in your organization. eXo Calendar provides
iCal support that you can use to exchange event/task information with other applications.
Searching for events/tasks in calendars also very convenient with full text search and advanced search with many criteria.

*eXo Mail is a mail client that is built with a variety of features designed to make your e-mail
experience more productive. It offers several ways to to view and organize your mails in-box and conversation.

*eXo Chat is a live web-based Instant Messaging system. It is a Jabber compliant chat implemented over the open xmpp protocol. With it, you can send instant message, exchange files real-time, store, export your conversation's history and sort by date, week, month. Group conversation very useful for co-working and group meeting online. eXo Chat comes in 2 flavours, the Chat application an webOS friendly windowed application and Chat Bar, an ubiquitous tiny chat bar that you can put on classic pages so that portal users can chat from wherever they are.


=============
 What's new?
=============
exo Collaboration Suite 1.3 features several noteworthy changes:

    * General
          o Upgraded to portal 2.5.5
          o Compatible with webos 1.5
          o Right to left (RTL) orientation support
          o Arabic translations
    * Calendar
          o Quick add event and task by javaScript for faster
          o Revamped participants and invitations dialog
          o Dynamic iCal generation for feeds (CalDAV and RSS)
    * Mail
          o IMAP folders listing and fetching
          o Return receipts
          o New layouts (vertical, horizontal, switch layout)
    * Address Book
          o Collected adresses adress book to collect contacts from Mail
          o Enhanced integration in Mail
    * Chat
          o Jabber chat server
          o Chat Bar application
          o WebOS friendly chat application
=========
 INSTALL
=========

- System Requirements
        Web Browser: IE6, IE7, FF2, FF3 (recommended), Safari.
        JVM: version 1.5.0_09 or higher
        Application Server : Tomcat, jboss
        Building Tools: Maven 2.0.6 and up
        openfire server version 3.4.5 for more information visit here : http://wiki.exoplatform.org/xwiki/bin/view/CS/Chat+Configuration

- Collaboration suite quick start guide
  Collaboration suite have 2 servers need to run at same time to use:
    +) exo-jboss: this is main jboss server include Collaboration web applications and all dependencies.     
    +) exo-openfire: a Jabber server used for Chat applications

Need to set the JAVA_HOME variable for run Collaboration suite's servers.
+) How to start Collaboration sute:
   * First thing first you need to give all script files the executable permission if you are in unix family environment.
   Use command: "chmod +x *.sh" (without quote) to have execute permission on these files.
   
   * NOTE for cygwin's user: the JAVA_HOME must be in MS Windows format like: "C:\Program Files\JDK 1.5"
    Example use: export JAVA_HOME=`cygpath -w "$JAVA_HOME"`; to convert unix like format to MS Windows format.
   
   * Start all servers by one command for Unix/Linux/cygwin environment:
      Go to exo-jboss/bin and run command:
      ./run.sh
   
   * Start exo-jboss server:
   
     +) On the Windows platform
       Open a DOS prompt command, go to exo-tomcat/bin and type the command:
         run.bat start

     +) On Unix/Linux/cygwin
       Open a terminal, go to exo-tomcat/bin and type the command:
         ./run.sh
    
   * Start exo-openfire server:
     +) On the Windows platform
       Open a DOS prompt command, go to exo-openfire/bin and type the command:
         openfired.exe

     +) On Unix/Linux
       Open a terminal, go to exo-openfire/bin and type the command:
         ./openfire start

-) How to access the eXo Collaboration Suite

* Enter one of the following addresses into your browser address bar:
   Classic :
      http://localhost:8080/portal
      http://localhost:8080/portal/public/classic
    WebOS : 
      http://localhost:8080/portal/private/classic/collaboration

You can log into the portal with the following accounts: root, john, marry, demo.
All those accounts have the default password "exo".

* Direct link to access applications in Collaboration suite:
    +) Calendar application: http://localhost:8080/portal/private/classic/calendar     
    +) Mail application: http://localhost:8080/portal/private/classic/mail     
    +) Address Book application: http://localhost:8080/portal/private/classic/contact     
    
  You will get login form if you are not yet logged in to Collaboration Suite.


===========
 RESOURCES
===========

     Company site        http://www.exoplatform.com
     Community JIRA      http://jira.exoplatform.org
     Community site      http://www.exoplatform.org
     Developers wiki     http://wiki.exoplatform.org


===========
 CHANGELOG
===========


- 1.3 RC1
** Bug
    * [CS-2867] - impossible to change time by mouse
    * [CS-3000] - can not subscribe to caldav by sunbird and window calendar
    * [CS-3002] - Server log print out NPE
    * [CS-3006] - Calendar: Don't show "Add new event" item when right click on existing event/task
    * [CS-3008] - still can add quick event in first time after delete all categories
    * [CS-3009] - Duplicate event category value in special case
    * [CS-3010] -  Need alert message when user input wrong password to join room
    * [CS-3011] - Don't show attachment of message in sent folder
    * [CS-3013] -  Normally there's no system message alerts some one has just joined room
    * [CS-3014] -  Show message require input "event summary" when delete participant
    * [CS-3015] - Error when close Add Calendar form without input
    * [CS-3018] - Still can add Event in shared calendar which user don't edit right in special case
    * [CS-3019] - events belong category is still displayed although that category isn't selected to view
    * [CS-3020] - don't show content of message when import
    * [CS-3021] - User can not add another into his personal contacts list after refuse the invitation
    * [CS-3022] - Quick add event: still allow set time although selected "All day" in special case
    * [CS-3028] -  should message to user known user can't do action with Address book of group
    * [CS-3029] - Unknow error when search mail with special character : ^
    * [CS-3031] - Little error in showing chat bar
    * [CS-3032] -  Page iterator is still shown when filter user
    * [CS-3035] - Public Calendar:User who don't have selected membership still have edit right
    * [CS-3076] - Chat portlet can not work on webos mode
    * [CS-3078] - Show error message when edit draft
    * [CS-3079] - Can not add event/task for group calendar
    * [CS-3080] - IE7: Join room form always show there's 2 pages in iterator when there's no room to join
    * [CS-3081] - Page iterator was not shown after added more than 10 contacts (need to refresh)

- 1.3 Beta2
** Bug
    * [CS-2599] - import address book or import calendar or import mail :  uploading is frozen when upload file again
    * [CS-2672] - safari: Month view: when click on event , event is drag& drop automatically
    * [CS-2812] - have some bugs occur when run quick chat
    * [CS-2835] - IE7: UI error at Room configuration form when set security
    * [CS-2840] - Occur wrong message when join room in special case
    * [CS-2844] - IE 7: IU error when show detail of message
    * [CS-2893] - IE7-WeOS: Can not do anything after logout/login while checking mail
    * [CS-2896] -  IE 7: while standing at search form : Can't open add/edit event/task form,Can't show menu when right click on calendar
    * [CS-2897] - Message ui error when click to show message detail
    * [CS-2898] - Commetd always send message when has chat bar portlet
    * [CS-2899] - unknown error when export the message, which has only header
    * [CS-2903] - room list does not show the rooms....
    * [CS-2909] - CS On webOS button does not open WebOS
    * [CS-2915] - Replace Save button in Password required popup by Cancel
    * [CS-2917] - Error in showing email address of receivers when "Reply to all" from Sent folder (auto put to cc field)
    * [CS-2918] - Always auto change priority of draft mail to "Normal" when edit a saved draft with high or low priority
    * [CS-2919] - Can't import email
    * [CS-2931] - Problem when click check mail button while selecting default folders (except Inbox)
    * [CS-1397] - multiple reminders at the same time     
    * [CS-2428] - Broken UI  at Create a new account form when Your display name field is so long     
    * [CS-2666] - Can invite others to join room when user does not have right          
    * [CS-2888] - ics import - duration is one day later
    * [CS-2900] - Chat bar is not floating
    * [CS-2906] - Show Message from : does not work     
    * [CS-2913] - UI error when try to some actions on email of account which was deleted in special case
    * [CS-2914] - IE7: Error in displaying Compose mail form when there're many email addresses
    * [CS-2916] - wrong default date time of event when create on month view
    * [CS-2929] - room name is not what user entered    
    * [CS-2936] - Right click on Calendar , UI error
 
** Improvement
    * [CS-569] - Fetch headers    
    * [CS-2254] - Thread date doesn't seem to change when a new email is added    
    * [CS-2895] - use buttons in message view  
    * [CS-2932] - Make imap the default choice in new account wizard
    * [CS-2933] - "Get only emails since" default value should be 10 days earlier

** New Feature
    * [CS-2385] - RTL CS apps
    * [CS-2536] - New Home page design for cs 1.3 with chat integrated
    * [CS-2677] - Chat Bar : Main UI  
    * [CS-2813] - Messages Sync

- 1.3 Beta1

** Bug
    * [CS-2486] - Email wrongly displayed
    * [CS-2710] - Unknown error when perform to some actions : share calendar,create event/task,edit calendar group,export calendar
    * [CS-2718] - UI error  when search mail
    * [CS-2719] - Change language to Arabic: UI error when add tag for Mail
    * [CS-2720] - has unexpected word at Add/Edit Message filter form
    * [CS-2722] - UI error when add tag for message in special case
    * [CS-2727] - Don't highlight   days in mini calendar/ year view  when These days have events/tasks
    * [CS-2728] - Don't shown event/task in List  View
    * [CS-2729] - Can not check mail
    * [CS-2767] - Can't export address book in special case
    * [CS-2768] - Lost message when drag and drop from a folder to Inbox folder, Or drag&drop message from a folder to Trash folder
    * [CS-2608] - have some UI error at Chat portlet
    * [CS-2627] - Lost content of conversation when show message from Today/This week/Last 30 days/beginning
    * [CS-2631] - occur wrong message when set security for room in special case
    * [CS-2669] - Overloaded methods in UICalendarPortlet.js
    * [CS-2717] - UI error when open mini calendar at edit account form
    * [CS-2721] - Change language to Arabic: UI error at detail header of mail and order of icons are fail  on FCK editor at compose mail form
    * [CS-2725] - Can't send the same file in special case
    * [CS-2726] - Don't show all user in user List at a public room
    * [CS-2766] - IE7: occur wrong message when perform  some actions
    * [CS-2769] - don't check mail any more after checked mail by limit checked date
    * [CS-2836] - Context menu is displayed incorrectly in IE 6

** Improvement
    * [CS-787] - Simplified new account wizard
    * [CS-1162] - Add contact to shared address book
    * [CS-1810] - Support for 'multi-part/related' mime type
    * [CS-2764] - Select multiple recipients from address book
    * [CS-2765] - Improve experience of giving answer to "Will You Attend ?" in Mail
    * [CS-2815] - Reply to all shortcut in message header
** New Feature
    * [CS-376] - Move events between calendars
    * [CS-839] - Add event participants by email
    * [CS-1008] - dynamic RSS/calDAV feeds
    * [CS-2568] - Custom Layout as a User Settings
   * [CS-764] - Revamp Participants tab
    * [CS-2509] - List Personal IMAP folders
    * [CS-2676] - Synchronize folder on click
    * [CS-2678] - Chat Bar : Status

