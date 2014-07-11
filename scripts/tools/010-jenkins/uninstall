#!/bin/sh

yum -y remove jenkins

# Remove Jenkins repo
rm -f /etc/yum.repos.d/jenkins.repo.*
rm -rf /var/lib/jenkins.orig /var/cache/jenkins.orig

# Remove configuration files
CFG_DIRS="/var/lib/jenkins /var/cache/jenkins /etc/sysconfig/jenkins*"
for dir in ${CFG_DIRS}; do rm -rf $dir || echo "$dir not found"; done

# Remove users and groups
userdel -rf jenkins
groupdel jenkins

