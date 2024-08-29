KADAI - The open source task management library
=================================================

[![Contributors](https://img.shields.io/github/contributors/kadai-io/kadai.svg)](https://github.com/kadai-io/kadai/graphs/contributors)
[![Styling](https://img.shields.io/badge/code_style-prettier-ff69b4.svg?style=flat-square)](https://prettier.io/)
[![CI](https://github.com/kadai-io/kadai/workflows/CI/badge.svg)](https://github.com/kadai-io/kadai/actions?query=workflow%3ACI)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Kadai_kadai&metric=alert_status)](https://sonarcloud.io/dashboard?id=Kadai_kadai)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kadai/kadai-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kadai/kadai-core)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

KADAI is a task management component open source library. It can be embedded into your application
or be operated standalone if appropriate. Beside the basic task management functionalities,
KADAI adds workbaskets and classifications to control and monitor a large amount of Tasks within
a larger organization.

* Web Site: http://kadai.io/
* Demo Environment: https://kadaiio.azurewebsites.net/kadai
* Issue Tracker: https://github.com/kadai-io/kadai/issues
* License: Apache License, Version 2.0  https://www.apache.org/licenses/LICENSE-2.0

---
_We're not aware of all installations of our Open Source project. However, we love_

* _listening to your feedback,_
* _discussing possible use cases with you,_
* _aligning the roadmap to your needs!_

📨 _Please [contact](#contact) us if you need consulting support._

---

# Table of Contents

* ✨ [Overview](#Overview)
    * [Tasks](#Tasks)
    * [Workbasket](#workbasket)
    * [Classification](#classification)
    * [Routing](#routing)
    * [Prioritization](#prioritization)
* [Components](#components)
    * [KADAI Lib](#Kadai-lib)
    * [KADAI REST API](#Kadai-rest-api)
    * [KADAI workplace](#Kadai-workplace)
    * [KADAI admin](#Kadai-admin)
    * [KADAI monitor](#Kadai-monitor)
* 🚀 [Getting Started](#getting-started)
    * [Requirements](#requirements)
    * [Wrapper Application](#wrapper-application)
        * [Spring Boot Example](#spring-boot-example)
        * [EJB Example](#ejb-example)
* [Customize Behaviour](#customize-behaviour)
* 📚 [Releases](#releases)
* 📨 [Contact](#contact)

# ✨Overview

## TASKS

Tasks are the main entity of KADAI. Each Task has its describing attributes like priority and due
date.

Furthermore each Task has a state.

And a Task holds a reference to the system and business object, it is associated with. This is
important since is meant to be a standalone component.

All Tasks are placed in a Workbasket to control and direct the handling of the Tasks.

![Tasklifecycle](docs/images/tasklifecycle.png)

## WORKBASKETS

Workbaskets are the main structure to distribute the Tasks to the available users. There are
personal, group/team and topic Workbaskets.

Workbaskets indicate the responsibility for a Task. The concepts of Workbaskets allow to
differentiate between the permissions or the skills required to complete a Tasks and the
determination who should complete the Tasks. This can diverge a lot in a larger organization.

![Workbaskets](docs/images/workbaskets.png)

## CLASSIFICATIONS

Classifications allow to identify the type of a Task. The Task derives some major attributes from
the Classification, such as the service level and the priority.

Classifications can be configured by a responsible business administrator to control the handling
of the Tasks.

## Routing

It offers various routing and distribution functions to assign incoming Tasks to the correct
Workbaskets, or to redistribute them between Workbaskets.

![Routing](docs/images/routing.png)

## Prioritization

The dynamic prioritization functions in allow you to control the processing sequence of
Tasks at any time. This ensures that the most important Tasks are always addressed first.

![Prioritization](docs/images/prio.png)

# Components

![Components](docs/images/components.png)

It splits up into five components:

* KADAI Lib
    * The Java library providing the Task management functions
* KADAI REST API
    * REST API to allow remote access to the KADAI system.
* KADAI workplace
    * Angular based web application to work on the Tasks
    * Sample Tasklist application you can use if appropriate
* KADAI admin
    * Angular based web application to configure the system
    * Supports Workbaskets and Classifications
* KADAI monitor
    * Angular based web application to monitor the status of the entire Task pool
    * Provides views and statistics about the conformance with defined services levels
    * Shows the workload of your organization and the individual teams

## Demo App Components Diagram

![Demo App Components](docs/images/componets_demo_app.png)

# 🚀Getting Started

As KADAI is meant to be integrated in the development environment and process of your organisation, you have to create
your own small integration project as a wrapper and starting point for your customisations.

We currently provide examples how to run KADAI as a Spring Boot Application or as a an Enterprise Application on Wildfly
Application Server.

If you are only interested in how KADAI looks and feel, you can try our Demo Environment instead.

## Requirements

Runtime Environment at least Java 17

Supported Databases:

* H2 (We test with 2.1.214*)
* Postgres (We test with 14.7*)
* DB2 (We test with 11.5.6*)
* Oracle (We test with 18c*)

_* other versions of the named databases should work also, but haven't been tested_

## Wrapper Application

### Spring Boot Example

We use the h2 database in this example.

See `rest/kadai-rest-spring-example-boot` and it dependencies

### EJB Example

We use a Wildfly Application Server in this example.

See `lib/kadai-cdi`.

# Customize Behaviour

KADAI allows to customize and modify it’s behaviour through the use of dedicated Service
Provider Interfaces (SPI). Each SPI defines an interface that can be implemented by custom code.
This is a common approach for Java developers to extend their applications. You can find out more
about the background and the details in the Java documentation:
https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html

In order to use an SPI within KADAI, you must

* create a class that implements the relevant interface.
* place that class into the classpath of your application
* provide a control file with full name of the SPI
  (e. g. io.kadai.spi.task.api.CreateTaskPreprocessor) in the subdirectory META-INF/services of
  the classpath. This control file must contain the fully qualified classname (including the
  package) of the class that implements the relevant interface. This control file is used by the
  ServiceLoader to load the custom class at runtime. The control file may contain multiple classes
  has implement the interface. Each implementation should be declared in a new line.

  All implementations will be used consecutively in the declaration order of the control file.

If you provide one or multiple implementations according to the description above, KADAI will invoke the implementations
at a specific point. The Javadoc of each SPI describes the conditions for the implementation to be executed.

Currently, KADAI provides the following SPIs:

* [`io.kadai.spi.history.api.KadaiHistory`](https://github.com/kadai-io/kadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/history/api/KadaiHistory.java)
* [`io.kadai.spi.priority.api.PriorityServiceProvider`](https://github.com/kadai-io/kadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/priority/api/PriorityServiceProvider.java)
* [`io.kadai.spi.routing.api.TaskRoutingProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/routing/api/TaskRoutingProvider.java)
* [`io.kadai.spi.task.api.AfterRequestChangesProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/AfterRequestChangesProvider.java)
* [`io.kadai.spi.task.api.AfterRequestReviewProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/AfterRequestReviewProvider.java)
* [`io.kadai.spi.task.api.BeforeRequestChangesProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/BeforeRequestChangesProvider.java)
* [`io.kadai.spi.task.api.BeforeRequestReviewProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/BeforeRequestReviewProvider.java)
* [`io.kadai.spi.task.api.CreateTaskPreprocessor`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/CreateTaskPreprocessor.java)
* [`io.kadai.spi.task.api.ReviewRequiredProvider`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/task/api/ReviewRequiredProvider.java)
* [`io.kadai.spi.user.api.RefreshUserPostprocessor`](https://github.com/kadai-iokadai/blob/master/lib/kadai-core/src/main/java/io/kadai/spi/user/api/RefreshUserPostprocessor.java)

# 📚Releases

The list of [releases](https://github.com/kadai-iokadai/releases) contains a detailed changelog.

We use [Semantic Versioning](https://semver.org/).

The following compatibility matrix shows the officially supported Spring version for each release.
Other combinations might also work but have not been tested.

| Release | SpringBoot |
|---------|------------|
| 7.0.0   | 2.7.16     |
| 6.3.2   | 2.7.14     |
| 6.2.2   | 2.7.12     |
| 6.1.1   | 2.7.10     |

# 📨Contact

If you have any questions or ideas feel free to create an [issue](https://github.com/kadai-iokadai/issues) or contact us
via [GitHub Discussions](https://github.com/orgs/kadai-iodiscussions).

We love listening to your feedback, and of course also discussing the project roadmap and possible use cases with you!

This open source project is being developed by [Envite Consulting GmbH](https://www.envite.de/)
and [envite consulting GmbH](https://envite.de/) with the support of the open source community.

---
[![envite consulting GmbH](docs/images/envite-black.png)](https://envite.de/)
---
