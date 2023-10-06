# HAnS (Helping Annotate Software) #
![Build](https://github.com/isselab/HAnS/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/22759.svg)](https://plugins.jetbrains.com/plugin/22759)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22759.svg)](https://plugins.jetbrains.com/plugin/22759)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

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
 + Fork the repository
 + Start adding your contribution as an annotated feature in the code
 + Build and Test
 + Make a pull request

To build and test you new snippet run the gradle [runIde] task.

Pull requests should be named like this: *`folder`*-*`featureName`*-*`contributor`*-*`description`*.

Folder can be either *`Feature`* or *`Bug`*. FeatureName should be one that is already in the featuremodel or some new feature name.
Lastly contributor is your git name and the description should be a short description of the new feature or the bug.

#### As a reviewer
The repository has policies in place (similar to other Git repositories) for which a team of members is responsible for ensuring the code quality of the features before they can be merged to the main/master branch. 
If you have experience with plugin development or want to help out by becoming a member of the review team, please reach out and contact any member in the existing team. 

##### Current review group
[![jhc github](https://img.shields.io/badge/GitHub-johmara-181717.svg?style=flat&logo=github)](https://www.github.com/johmara)
[![jhc github](https://img.shields.io/badge/GitHub-janssonherman-181717.svg?style=flat&logo=github)](https://www.github.com/janssonherman)

### Who do I talk to? 
[![jhc github](https://img.shields.io/badge/GitHub-johmara-181717.svg?style=flat&logo=github)](https://www.github.com/johmara)
[![jhc github](https://img.shields.io/badge/GitHub-janssonherman-181717.svg?style=flat&logo=github)](https://www.github.com/janssonherman)

#### Research group
[![jhc github](https://img.shields.io/badge/GitHub-isselab-181717.svg?style=flat&logo=github)](https://www.github.com/isselab)
[![](https://img.shields.io/website.svg?down_color=red&down_message=down&up_color=green&up_message=isselab.org&url=http%3A%2F%2Fshields.io)](https://www.isselab.org)
#### Chair of Software Engineering   
[![Chair of Software Engineering](https://img.shields.io/website.svg?down_color=red&down_message=down&up_color=green&up_message=se.ruhr-uni-bochum.de&url=http%3A%2F%2Fshields.io)](http://se.rub.de)

#### Papers published:
##### 2021
- Master Thesis by Johan Martinson & Herman Jansson  
  [![Johan & Herman](https://zenodo.org/badge/DOI/20.500.12380/302926.svg)](https://doi.org/20.500.12380/302926)
- HAnS: IDE-based editing support for embedded feature annotations by Johan Martinson, Herman Jansson, Mukelabai Mukelabai, Thorsten Berger, Alexandre Bergel, and Truong Ho-Quang.  
  [![](https://zenodo.org/badge/DOI/10.1145/3461002.3473072.svg)](https://doi.org/10.1145/3461002.3473072)


### [Contributors](CONTRIBUTORS.md)

