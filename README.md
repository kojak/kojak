Kojak - "Koji in a box"
========================

Kojak is fully integrated DevOps pipeline that uses Openshift to deploy applications in the cloud.

Kojak started off as a collection of scripts that can be used to automate the installation and configuration of the Koji build system. It was part of a productization effort to encourage the adoption of Koji for use in development, testing and staging environments.  The scripts included in this repository can be used to create a fully operational Koji instance on the local system or optionally as virtual machine utilizing either the Libvirt visualization API and a Kickstart file or Vagrant and VirtualBox.  The virtual machine is based on a stock vanilla CentOS installation, to provide an automated "out of the box" solution, which may be used for development, testing and educational purposes.

Tool Chain
----------

Kojak ships with an, industry reconised, opensource toolchain designed to allow developers to build, test, deploy, promote, release and replicate product builds in a consistent and predictable way.  The long term goal is to provide a DevOps platform to allow users to familiarize themselves with the DevOps aproach and tools.   

Installation Prerequisites
---------------------------

Kojak is a virtual appliance and it not recommended that you insall directly on your worksation.  The Installation options will build a virtual machine for you which will be accessible from your local host.  If you already have a virtual machine you can install Kojak directly on that machine and skip the virtual machine provisioning stage.  Kojak has been successfully installed and tested on CentOS and RHEL 6+.

For more information about virtualization see below:

CentOS http://wiki.centos.org/HowTos#head-fb1ff7e71fb5f2f511cda8c68cb6ba5f6e8decae 

Fedora http://fedoraproject.org/wiki/Virtualization?rd=Tools/Virtualization

It is recommended that the system be updated before beginning the installation.  A fast internet connection is also required in order to facilitate the downloading of any package dependancies.

Minimum System Requirements
--------------------------- 

The virtual machine is configured with the following default specifications:

1. 4GB RAM
2. 8GB Disk Space

Your local host should be configured with the following minimum specifications:

1. Centos 6.5 or Fedora 18+ with virtualization package group
2. 8GB RAM
3. 24 GB Disk Space 

Installation Instructions
------------------------

Checkout the contents of the Kojak git repository to a suitable directory on your host.
```
[root@localhost]# cd ~/workspace
[root@localhost]# git clone https://github.com/sbadakhc/kojak.git
```

As the root user execute kojak script.  You will be prompted about the type of installation you want to conduct.  If you choose to create a virtual machine you will be further prompted to supply some configuration options.  You can simply accept the default options if you have no special requirements.
```
[root@localhost]# cd ~/kojak
[root@localhost]# ./kojak
1. Create VM
```

Select from a choice of Virtual machine types from the menu options.
```
1. Libvirt
2. VirtualBox
3. Quit
```

The VirtualBox installation ships as a fixed configuration.
For Libvirt you can customise the installation but selecting the default option is generally good enough.
```
1. Default
```

The VirtualBox installation will log you in automagically to your virtual machine where you will have to manually continue with the configuration.
```
[root@localhost]# cd ~/workspace
[root@localhost]# yum -y install git
[root@localhost]# git clone https://github.com/sbadakhc/kojak.git
[root@localhost]# cd ~/workspace/kojak
[root@localhost]# ./kojak
2. Install
```

You can access the Libvirt virtual machine by logging in to the address 192.168.122.2 as the root user and execute the Kojak script once again.  It will be located under /opt/kojak.  This time you can select the "Install" option from the menu.
```
[root@localhost]# ssh root@192.168.122.2
[root@localhost]# cd ~/workspace
[root@localhost]# ./kojak
2. Install
```

After the installation is complete you can opt to configure the system with base RCM tagging heirarchy.  This will get you up and running and building packages quickly.  Simply execute the Kojak script again and select the "Configure" option from the menu.
```
[root@localhost]# ./kojak
3. Configure
```

At this stage the system is fully installed and configured.  

Browsing to the Jenkins web interface you will see a preconfigured build task.  Simply execute the build task and wait for it to complete.  This "development" build can be tracked via the Jenkins console and if successful it will be submitted to the Koji instance for building.  

You can track the result of your "staging" build via the Koji web interface.  If your build is successful then you are in pretty good shape to submit your build to your RCM team ;) Building with Kojak allows you to leverage Jenkins for Continous Integration and Aprox for repository management in a preconfigured and standardised way.

For more inforation about using Koji see:

https://fedoraproject.org/wiki/Koji for more details about using Koji.

Configuration Notes
-------------------
The Kojak virtual machine is configured with a set of default options.  Executing the kojak script will allow you to modify and save these variables as required.  Installation directories, ISO location and virtual machine resources allocations (Mem, CPU and Storage etc.) can all be reconfigured as required.  The appliance is configured with with a static address taken from the pool of IP addresses from the "default" network that is configured with libvirt.

You can access the virtual machine via ssh at 192.168.122.2 using the following credentials:

1. username: root
2. password: root

Currently Kojak uses SSL certificates as the preferred method of authentication.  To utilize the client certificate for browser based logins you will need to import the certificate.  The certificates can be accessed from /home/koji/.koji.  Certificates are created for a default set of users which includes koji, kojiadmin, kojira and 3 kojibuilders.

The Koji web interface is accessible via http://devops.example.com/koji

Jenkins configured with the Koji plugin can be configured via the Jenkins GUI via http://devops.example.com:8081

Artifcatory configured with a standard set of repos is available via http://devops.example.com:8090

Known Issues and How to Report Them
-----------------------------------

Please follow this template while reporting an issue:

Component: Host, Guest (OS version), Koji, Apache HTTP etc.

Description: description of the situation 

Steps to reproduce: clear description of how to reproduce the issue, as ordered list

Expected result: the result that was expected

Actual result: the actual result including error messages, exceptions etc.

See http://github.com/sbadakhc/kojak/issues?state=open

Discussion
----------

Join our community at https://plus.google.com/u/0/communities/103028582394768375364
