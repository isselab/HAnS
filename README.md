# HAnS (Helping Annotate Software)

![Build](https://github.com/isselab/HAnS/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/22759.svg)](https://plugins.jetbrains.com/plugin/22759)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22759.svg)](https://plugins.jetbrains.com/plugin/22759)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Static Badge](https://img.shields.io/badge/-HAnS%20Community-darkgrey?logo=Discord&logoColor=%23FFF&labelColor=%235865F2&color=%23000000)](https://discord.gg/7hgRnRnW9r)

<!-- Plugin description -->
HAnS is a JetBrains IDE plugin that supports feature-oriented software development by enabling developers to annotate, track, and analyze features throughout their codebase. The plugin provides comprehensive tooling for feature location, mapping, and metrics collection in software systems.

## Key Features

### Feature Modeling & Annotation
- **Feature Model Language** - Define hierarchical feature models using the `.feature-model` format
- **Code Annotation** - Annotate code fragments with feature markers using lightweight comments (`&begin[Feature]`, `&end[Feature]`, `&line[Feature]`)
- **File & Folder Mapping** - Map entire files or directories to features using `.feature-to-file` and `.feature-to-folder` mappings
- **Live Templates** - Quick insertion of feature annotations with predefined templates
- **Intelligent Completion** - Context-aware code completion for feature names and annotation syntax

### IDE Integration
- **Feature Model View** - Visual tool window displaying the hierarchical feature structure
- **Feature Metrics View** - Bottom panel showing calculated metrics including:
  - Line Count per feature
  - Feature-to-File mappings
  - Feature Scattering degree
  - Feature Tangling degree
- **Traffic Light Indicator** - Visual indicator showing file/folder feature mapping status
- **Syntax Highlighting** - Custom syntax highlighting for all feature annotation languages with configurable color schemes (Darcula and Default themes)

### Refactoring & Navigation
- **Smart Referencing** - Navigate between feature definitions and their usages
- **Feature Renaming** - Rename features across the entire project with automatic reference updates
- **Find Usages** - Locate all occurrences of a feature throughout the codebase
- **Quick Fixes** - Automated fixes for unassigned features and annotation errors

### Extensibility
- **Extension Points** - Plugin architecture with callbacks for metrics and feature tracking
- **Service Layer** - Metrics service and highlighter service for plugin extensions
- **Background Tasks** - Asynchronous processing for metrics calculation and feature analysis

### [Demo Video](https://youtu.be/cx_-ZshHLgA)

<!-- Plugin description end -->

## Getting Started

### Installation

**From JetBrains Marketplace (Recommended):**

<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "HAnS"</kbd> > <kbd>Install</kbd>

**Manual Installation:**

Download the [latest release](https://github.com/isselab/HAnS/releases/latest) and install using:
<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

### Compatibility

- **Supported IDEs**: All JetBrains IDEs (IntelliJ IDEA, Rider, PyCharm, WebStorm, etc.)
- **Minimum Build**: 2025.1 (251)
- **Java Version**: 21+

### Quick Start

1. Create a `.feature-model` file in your project root to define your feature hierarchy
2. Use live templates or quick actions to annotate code with features
3. View your feature structure in the Feature Model View tool window
4. Access feature metrics in the Feature Metrics View panel
5. Track file/folder mappings with the traffic light indicator

## Contributing

We welcome contributions from the community! HAnS itself is annotated with features, serving as a practical example of the plugin's capabilities.

### How to Contribute

1. **Fork the repository**
2. **Annotate your contribution** - Add feature annotations to your code following the existing patterns
4. **Build and test** - Run the `runIde` Gradle task to test your changes, write unit tests when appropriate
5. **Submit a pull request**

### Pull Request Guidelines

Name your PR using this format: `[Folder]-[FeatureName]-[Contributor]-[Description]`

- **Folder**: Either `Feature` or `Bug`
- **FeatureName**: Use an existing feature from the feature model or propose a new one
- **Contributor**: Your GitHub username
- **Description**: Brief description of the change

### Code Review Team

All contributions are reviewed by our core team to ensure code quality and maintainability. Interested in joining the review team? Contact any current reviewer.

#### Current Reviewers
[![jhc github](https://img.shields.io/badge/GitHub-johmara-181717.svg?style=flat&logo=github)](https://www.github.com/johmara)
[![jhc github](https://img.shields.io/badge/GitHub-janssonherman-181717.svg?style=flat&logo=github)](https://www.github.com/janssonherman)
[![jhc github](https://img.shields.io/badge/GitHub-HerrKermet-181717.svg?style=flat&logo=github)](https://www.github.com/HerrKermet)

## Research & Publications

HAnS is developed and maintained by academic research groups focused on software engineering and feature-oriented development.

### Research Group

[![ISSELab](https://img.shields.io/badge/GitHub-isselab-181717.svg?style=flat&logo=github)](https://www.github.com/isselab)
[![ISSELab Website](https://img.shields.io/website.svg?down_color=red&down_message=down&up_color=green&up_message=isselab.org&url=http%3A%2F%2Fshields.io)](https://se.rub.de/research-group/)

[![Chair of Software Engineering](https://img.shields.io/website.svg?down_color=red&down_message=down&up_color=green&up_message=se.ruhr-uni-bochum.de&url=http%3A%2F%2Fshields.io)](http://se.rub.de)

### Publications
**2025**
- **Lightweight Visualization of Software Features with HAnS-viz** by Johan Martinson, Kevin Hermann, Riman Houbbi, David Stechow, Thorsten Berger

    [![DOI](https://zenodo.org/badge/DOI/10.1145/3748269.3748487.svg)](https://doi.org/10.1145/3748269.3748487)

**2024**
- **An IDE Plugin for Clone Management** by Ahmad Al Shihabi, Jan Sollmann, Johan Martinson, Wardah Mahmood, Thorsten Berger
  
  [![DOI](https://zenodo.org/badge/DOI/10.1145/3646548.3678298.svg)](https://doi.org/10.1145/3646548.3678298)

**2021**
- **Master Thesis** by Johan Martinson & Herman Jansson
  
  [![DOI](https://zenodo.org/badge/DOI/20.500.12380/302926.svg)](https://doi.org/20.500.12380/302926)

- **HAnS: IDE-based editing support for embedded feature annotations** by Johan Martinson, Herman Jansson, Mukelabai Mukelabai, Thorsten Berger, Alexandre Bergel, and Truong Ho-Quang
  
  [![DOI](https://zenodo.org/badge/DOI/10.1145/3461002.3473072.svg)](https://doi.org/10.1145/3461002.3473072)

### Annotated Datasets

Example projects using HAnS for feature annotation:

- [EASE Lab Workspace](https://bitbucket.org/easelab/workspace/projects/EA)
- [Snake Game](https://github.com/johmara/Snake)
- [HAnS-Viz Visualization Tool](https://github.com/isselab/HAnS-Viz)

## Contact & Support

### Maintainers
[![johmara](https://img.shields.io/badge/GitHub-johmara-181717.svg?style=flat&logo=github)](https://www.github.com/johmara)

### Community
Join our [Discord community](https://discord.gg/7hgRnRnW9r) for discussions, support, and updates.

### Contributors
See our [CONTRIBUTORS.md](CONTRIBUTORS.md) for a full list of project contributors.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

