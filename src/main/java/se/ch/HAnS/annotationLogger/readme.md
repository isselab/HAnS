# Time Logging Plugin

## Overview
The annotation logger is a tool in the HAnS plugin designed to measure the time spent on various activities in a codebase. It captures and logs the time spent on different annotation types and user activities in a project related to the HaNS plugin.

## How It Works
The annotation logger works by listening for certain events in the code such as adding, removing, or replacing annotations, as well as keystrokes and mouse events indicating user activity. The plugin periodically checks for idle time and logs the total time spent during active sessions. When a session is considered idle, the plugin resets the session time and updates the log files. Here are all the classes used for the annotation logger:

- The `CustomDocumentListener` class is an important part of the Time Logging Plugin. This class is responsible for listening to changes in the codebase's PSI (Program Structure Interface) Tree. These changes can include the addition, removal, or replacement of annotation comments.


- The `AnnotationEventHandler` class is responsible for handling annotation events. It logs the time spent on different annotation tasks such as line annotation, block annotation, feature-to-file, feature-to-folder, and feature model.


- The `SessionTracker` class tracks the total time a user has been active in the IDE. It listens for mouse and keyboard events and updates the total active time if the user is not considered idle (no activity for a certain threshold).


- The `CustomTimer` class helps manage and calculate time. It checks if enough time has passed since the last logged event, updates the last logged time, and provides the current date and time.


- The `LogWriter` class in the Time Logging Plugin is responsible for logging events and writing them to specific files. It maintains three types of files: a simple text log, a JSON log for individual events, and a JSON log for entire sessions. This class also interacts with a MongoDB database handler for potential database operations.


- The `MongoDBHandler` class in the Time Logging Plugin is responsible for interacting with a MongoDB database. It connects to a MongoDB instance, interacts with a specific database and collection, and provides a method to insert log data.


- The `CustomVirtualFileListener` class listens for file-related events, specifically the feautre files related to HAnS (".feature-to-file", ".feature-to-folder", and "feature-model"), and it logs when the files are created and deleted.


- The `CustomDocumentEventListener` class handles the instant deletion of an annotation, specifically for the &block and &line annotation. It the logs the time the user took to delete that annotation. So it takes the time the user started to highlight the feature as the starting time, and the instant deletion of the annotation as the end time.


- The `HansAnnotationLoggingPageClass` class represents the configuration page for Hans Annotation Logging, it gives the user the ability to choose where they want to store the logged annotation, either remotely (to mongoDB), locally, or if they don´t want to store it at all. 


- The `PluginStartupActivity` class handles the plugin start-up activities whenever a project is opened. It sets up the customDocumentListeners, avoids duplication, initializes  the listeners and services such as ProjectCloseService.


- The `ProjectCloseListener` class listens to project close events and performs activities when the project is closed. Such as logging the annotation sessions, annotation total time, session time, resetting the log file, and resetting the session tracker.


- The `ProjectCloseService` class handles the project close service. It is responsible for the initiation and management project close events, and these events are present in the ProjectCloseListener.



## How to Use
The logging tool is activated off when using the HAnS plugin. To use it, simply press the settings icon at the top left of intelliJ:

![](\materials\HansSettingPage.png)

Then press `settings`, and finally at the bottom of the new screen there should be a `HAnS Annotation Logging` tab. Just click on that tab and then simply select where you want store the annotation file.