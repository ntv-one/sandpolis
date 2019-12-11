<p align="center">
	<img src="https://s3.us-east-2.amazonaws.com/github.sandpolis.com/header.png" />
</p>

![GitHub release](https://img.shields.io/github/release-pre/Subterranean-Security/Sandpolis.svg?color=blue)
[![Build Status](https://travis-ci.org/Subterranean-Security/Sandpolis.svg?branch=master)](https://travis-ci.org/Subterranean-Security/Sandpolis)
[![Build status](https://ci.appveyor.com/api/projects/status/8a2xdoy8pt21k77g?svg=true)](https://ci.appveyor.com/project/cilki/sandpolis)

**Sandpolis** is a cross-platform remote administration framework that can manage servers, desktop computers, mobile devices, and anything else that runs Java (except for your printers, Blu-Ray players, refrigerators, etc).

Sandpolis is targeted at sysadmins and power users, but can be used by anyone once it's configured. Most users will want to use Sandpolis via the JavaFX desktop application, but alternative user interfaces (like web and mobile applications) are planned.

**Sandpolis is unfinished and therefore should only be used in a testing environment**. There are a few pre-releases available, but don't expect much from them until things are more complete.

### How
There are three components in any Sandpolis network:

- a **client** agent installed on a remote computer
- a **viewer** interface that you can use to manage client instances
- a **server** that facilitates communication between clients and viewers

For convenience, there are many ways to install the Sandpolis client on your machines. The simplest method is to generate an executable file via the viewer interface and manually run it on each remote machine. Once installed, the client agent can automatically update itself independently of the server.

Clients maintain a persistent connection to the server, and viewers maintain a temporary connection to the server or directly to clients. This allows tasks to continue running in the background while viewers are not connected.

### Why
Sandpolis is somewhat like a fusion of [TeamViewer](https://www.teamviewer.com) and Canonical's [Landscape](https://landscape.canonical.com). The overall architecture and intended usage of Sandpolis is very similar to Landscape, but Sandpolis also provides features like remote desktop that don't fit in Landscape. Sandpolis isn't intended to replace TeamViewer or Landscape, but rather it serves the middle-ground between the two.

Here are some fundamental qualities that Sandpolis tries to achieve:

- compatible with as many operating systems as possible
- flexible, configurable, and easily extensible to niche applications via plugins
- uncompromising on performance and security
- low latency and high concurrency
- user friendly

### What it isn't
Sandpolis is **not** intended to be used on unauthorized systems, to manage a botnet, or for any other illegal activity. 

There are many _remote administration tools_ out there that claim to be for legitimate use, but have strange features like ransomware, password stealers, and network flooders. While there may be a legitimate use-case for all of those, the reality is that more people are going to use those features for harm than for good. Fortunately, most _remote administration tools_ are so brittle and poorly designed that they are practically useless anyway.

### Installing
- **Windows**
`// TODO`

- **Arch Linux**
    - Download the [development package](https://aur.archlinux.org/pkgbase/sandpolis-git) or the [stable package](https://aur.archlinux.org/pkgbase/sandpolis) from the AUR
    - Extract and install with `makepkg -si`
    - The server daemon can be started with `systemctl start sandpolisd`

- **Debian/Ubuntu**
`// TODO`

- **MacOS**
`// TODO`

- **Docker**
    - The server can be started with: `sudo docker run sandpolis/sandpolis-server`
    - See [DockerHub](https://hub.docker.com/r/sandpolis/sandpolis-server) for more information

### Building and Testing
The build is divided into sets of **instance modules**, **library modules**, and **plugin modules**:

|Module Type| Location | Description|
|-----------|----------|------------|
| Instance Module | `/` | Contains application modules like the server or client |
| Library Module | `/module` | Contains common library modules |
| Plugin Module | `/plugin` | Contains Sandpolis plugins |
| Gradle Plugin | `/gradle` | Contains Gradle plugins |

Building the entire project is as easy as running `./gradlew assemble` from the project's root directory. This will output (almost ready-to-run) executables into the `build/libs` directories of each instance module.

Setting up the execution environment for testing can be a burden, so the `com.sandpolis.gradle.deploy` plugin was created to make deploying an instance to any machine possible with a single command. To use it, create an entry in `remote.gradle` for the machine you wish to execute on and run the corresponding Gradle task. For example, `./gradlew :com.sandpolis.server:user@localhost` will deploy the server instance to the local machine via SSH and run it in a `screen` session.

### A Brief History of Sandpolis
Sandpolis evolved out of an application called Crimson which was initially released in 2013. Retrospectively, Crimson was an experiment in what kinds of features an administration system can (and should) contain. Overall, Crimson was a valuable experience which heavily informed the development of Sandpolis.

After four years of sporadic development, Crimson was finally abandoned and Sandpolis was created to take its place. The old [repository](https://github.com/Subterranean-Security/Crimson) is now archived for the software archaeologists out there. Although almost none of the Crimson codebase has survived into Sandpolis, the overall goal has remained the same: **build the ultimate system management utility**.
