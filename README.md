Kojak - "Koji in an box"
========================

Kojak is a Koji virtual appliance complete with Mead extensions and Maven tool chain for building and deploying Java
applications.  It is part of a productization effort to encourage the adoption of the Kojak/Mead build system.  The 
scripts included in this repository create a fully operational Koji/Mead virtual machine running on Fedora 18.  They 
utilize the Libvirtd visualization API and a kickstart file, based on a vanilla Fedora installation, to provide an
automated "out of the box" solution, which may be used for development and educational purposes.

Kojak's tool chain allows developers to build, test, deploy, verfiy and replicate product builds and their associated
repositories.

See the links below for a details about the tools shipped with Kojak

1. Aprox - http://github.com/jdcasey/aprox
2. Atlas - http://github.com/jdcasey/atlas
3. Cartographer - http://github.com/jdcasey/cartographer
4. Galley - http://github.com/jdcasey/galley
5. Koji - http://fedoraproject.org/wiki/Koji
6. maven-repository-builder - http://github.com/jboss-eap/maven-repository-builder
7. python-javatools - http://github.com/obriencj/python-javatools.git
8. buildmetadata-maven-plugin - http://github.com/sbadakhc/buildmetadata-maven-plugin.git

Installation Prerequisites
---------------------------

This installation comprises of a virtual appliance server which is used to build and deploy the virtual appliance.
Kojak has been successfully installed and tested on Fedora 18. 

See http://fedoraproject.org/wiki/Getting_started_with_virtualization for more information

It is recommended that the system be updated before beginning the installation.  A fast internet connection and is also 
required in order to facilitate the downloading of any package dependancies.

Minimum System Requirements
--------------------------- 

The virtual appliance is configured with the following default specifications:

1. 4GB RAM
2. 32 GB Disk Space

The Virtual Appilance Server should be configured with the following minimum specifications:

1. Fedora 18 with virtualization package group
2. 8GB RAM
3. 120 GB Disk Space 

Installation Instructions
------------------------

1.  Checkout the contents of the Kojak git repository to a suitable directory on the Virtual Appliance Server.
2.  Check and modify the files under the kojak directroy to suit your particular environment.
3.  Execute install.sh to create the Kojak virtual appliance.  The script requires root privileges to run.
4.  Point your browser to 192.168.122.2 and check the states of the tasks.

Configuration Notes
-------------------

The Kojak virtual appliance is configured with a set of default options.  These can be easily modified by editing the 
variables in install.sh.  Installation directories, iso location and virtual machine resources allocations
(Mem, CPU and Storage etc) can all be reconfigured as required.  The appliance is configured with with a static ipaddress 
taken from the pool of ip addresses from the "default" network that is configured with libvirt.

You can access the appliance via ssh at 192.168.122.2 using the following credentials:

1. username: root
2. password: root

Currently Kojak uses SSL certificates as the preferred method of authentication. To utilize the client certificate for
browser based logins you will need to import the certificate. The certificate is can be accessed from /home/koji/.koji.
Certificates are created for a default set of users which includes koji, kojiadmin, kojira and 3 kojibuilders.

The Maven tool chain is located in the koji users workspace directory along with example configurations. 

Known Issues
------------

Access to http://download.devel.redhat.com/ is required for the current version of Maven that is used.
