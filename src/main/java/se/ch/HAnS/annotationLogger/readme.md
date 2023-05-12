# Time Logging Plugin

## Overview
The annotation logger is a tool in the HAnS plugin designed to measure the time spent on various activities in a codebase. It captures and logs the time spent on different annotation types and user activities in a project related to the HaNS plugin.

## How It Works
The plugin works by listening for certain events in the codebase such as adding, removing, or replacing annotation comments, as well as keystrokes and mouse events indicating user activity.

- The `CustomDocumentListener` class is an important part of the Time Logging Plugin. This class is responsible for listening to changes in the codebase's PSI (Program Structure Interface) Tree. These changes can include the addition, removal, or replacement of annotation comments.

- The `AnnotationEventHandler` class is responsible for handling annotation events. It logs the time spent on different annotation tasks such as line annotation, block annotation, feature-to-file, feature-to-folder, and feature model.

- The `SessionTracker` class tracks the total time a user has been active in the IDE. It listens for mouse and keyboard events and updates the total active time if the user is not considered idle (no activity for a certain threshold).

- The `CustomTimer` class helps manage and calculate time. It checks if enough time has passed since the last logged event, updates the last logged time, and provides the current date and time.

- The `LogWriter` class in the Time Logging Plugin is responsible for logging events and writing them to specific files. It maintains three types of files: a simple text log, a JSON log for individual events, and a JSON log for entire sessions. This class also interacts with a MongoDB database handler for potential database operations.

- The `MongoDBHandler` class in the Time Logging Plugin is responsible for interacting with a MongoDB database. It connects to a MongoDB instance, interacts with a specific database and collection, and provides a method to insert log data.

The plugin periodically checks for idle time and logs the total time spent during active sessions. When a session is considered idle, the plugin resets the session time and updates the log files.

## How to Use
The logging tool is activated automatically when using the HAnS plugin. However, we will add an option to disable it in the preference tab in the near future