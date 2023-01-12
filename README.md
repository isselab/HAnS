# HAnS - Helping Annotate Software #
*`Version 1.0`*

HAnS is a plugin that aims to help developers with annotating software. The annotations themselves are a proven help with
locating features, so-called feature location.

[Demo video](https://youtu.be/cx_-ZshHLgA)

HAnS-text supports:
* Feature Annotation Languages
* Syntax Highlighting
* Code Completion
* Feature Model View
* Referencing
* Refactoring
* Live templates
* New files

### How to install? ###

HAnS text can easily be installed as a plugin for IntelliJ. To set it up you need IntelliJ
2021.1 or later installed and the HAnS 1.0 plugin downloaded.

*Comming to the IntelliJ plugin marketplace shortly*

### Contribution guidelines ###
You are free to contribute to this repository but configure 

#### Getting started
 + Clone the repository.
 + Start adding your code for your new feature or bugfix
 + Create a new branch
 + Build and Test
 + make a pull request

To build and test you new snippet run the gradle [runIde] task.

Branches should be named like this: *`folder`*/*`featureName`*/*`contributor`*-*`description`*.

Folder can be either *`Feature`* or *`Bug`*. Featurename should be one that is already in the featuremodel or some new featurename.
Lastly contributor is your git name and the description should be a short description of the new feature or the bug.

#### As a reviewer
The repository have policies in place (similar to other Git repositories) where we have a team of members that are responsible for ensuring the code quality of the features before they can be merge to the main/master branch. 
If you have experience with plugin development or want to help out by becoming a member of the review team, 
please reach out and contact any member in the existing team. 

Information on which people to contact/are currently are in the review team can be found here:
+ @Johan Martinson
+ @Herman Jansson

#### Code Quality
Given that the plugin will have a widespread use and most likely be used repeatedly, it is essential that the code is of quality. 
For that reason we have policies in place where reviewers are required to review and accept the code before it is 
accepted into the main branch.

### Who do I talk to? ###
* @Johan Martinson or @Herman Jansson
* Other community or team contact: Mukelabai Mukelabai 