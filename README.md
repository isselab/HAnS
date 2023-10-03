# HAnS (Helping Annotate Software) #
![Build](https://github.com/isselab/HAnS/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/22759.svg)](https://plugins.jetbrains.com/plugin/22759)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22759.svg)](https://plugins.jetbrains.com/plugin/22759)

<!-- Plugin description -->
HAnS is a plugin that aims to help developers with annotating software assets with features.

The features of HAnS are:
  - Feature Annotation Languages
  - Mapping code fragments to features
  - Mapping files or directories to features
  - Completion aid when annotating
  - Feature Model View
  - Referencing
  - Renaming features
  - Quick fixes
  - Live templates

### [Demo video](https://youtu.be/cx_-ZshHLgA)



<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "HAnS"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/isselab/HAnS/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

### Contribution guidelines ###
You are free to contribute to this repository.

#### Getting started
 + Fork the repository.
 + Start adding your code for your new feature or bugfix
 + Build and Test
 + make a pull request

To build and test you new snippet run the gradle [runIde] task.

Pull requests should be named like this: *`folder`*-*`featureName`*-*`contributor`*-*`description`*.

Folder can be either *`Feature`* or *`Bug`*. Featurename should be one that is already in the featuremodel or some new featurename.
Lastly contributor is your git name and the description should be a short description of the new feature or the bug.

#### As a reviewer
The repository have policies in place (similar to other Git repositories) where we have a team of members that are responsible for ensuring the code quality of the features before they can be merged to the main/master branch. 
If you have experience with plugin development or want to help out by becoming a member of the review team, 
please reach out and contact any member in the existing team. 

Information on which people to contact/are currently are in the review team can be found here:
+ [@Johan Martinson](https://www.github.com/johmara)
+ [@Herman Jansson](https://github.com/janssonherman)

#### Code Quality
Given that the plugin will have a widespread use and most likely be used repeatedly, it is essential that the code is of quality. 
For that reason we have policies in place where reviewers are required to review and accept the code before it is 
accepted into the main branch.

### Who do I talk to? ###
* [@Johan Martinson](https://www.github.com/johmara) or [@Herman Jansson](https://github.com/janssonherman)
* Other community or team contact: Mukelabai Mukelabai 