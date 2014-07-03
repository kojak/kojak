#!/bin/sh
#
# RCM Middleware Tools Tags

# Uncomment to debug
#set -x 

# Declare variables
export PROG="koji"
export USER="koji"
export PRODUCT="rcm-mw-tools"
export ARCH="x86_64"
export MAVEN="maven3-3.0.3-4"

# Begin Koji configuration
su -l ${USER} -c "
# Add product tags
${PROG} add-tag ${PRODUCT} --arches ${ARCH} --maven-support --include-all
${PROG} add-tag ${PRODUCT}-candidate --arches ${ARCH} --maven-support --include-all
${PROG} add-tag ${PRODUCT}-build --arches ${ARCH} --maven-support --include-all

# Add tag inheritance
${PROG} add-tag-inheritance ${PRODUCT}-candidate ${PRODUCT} --priority 0
${PROG} add-tag-inheritance ${PRODUCT}-build ${PRODUCT}-candidate --priority 0

# Add target
${PROG} add-target ${PRODUCT}-candidate ${PRODUCT}-build ${PRODUCT}-candidate

# Add external repo
${PROG} add-external-repo -t ${PRODUCT}-build centos-mirror http://mirror.centos.org/centos/6/os/x86_64/

# Add groups
# build
${PROG} add-group ${PRODUCT}-build build
${PROG} add-group-pkg ${PRODUCT}-build build bash bzip2 cpio diffutils fedora-release findutils gawk gcc gcc-c++ info make redhat-rpm-config rpm-build sed shadow-utils unzip util-linux-ng which xz

# srpm-build
${PROG} add-group ${PRODUCT}-build srpm-build
${PROG} add-group-pkg ${PRODUCT}-build srpm-build bash curl cvs fedora-release fedpkg gnupg2 make redhat-rpm-config rpm-build shadow-utils

# appliance-build
${PROG} add-group ${PRODUCT}-build appliance-build
${PROG} add-group-pkg ${PRODUCT}-build appliance-build appliance-tools bash coreutils grub parted perl policycoreutils selinux-policy shadow-utils

# maven-build
${PROG} add-group ${PRODUCT}-build maven-build
${PROG} add-group-pkg ${PRODUCT}-build maven-build bash coreutils java-1.7.0-openjdk-devel maven3 subversion liberation-sans-fonts liberation-serif-fonts liberation-mono-fonts git

# livecd-build
${PROG} add-group ${PRODUCT}-build livecd-build
${PROG} add-group-pkg ${PRODUCT}-build livecd-build bash bzip2 coreutils cpio diffutils fedora-logos fedora-release findutils gawk gcc gcc-c++ grep gzip info livecd-tools make patch policycoreutils python-dbus redhat-rpm-config rpm-build sed selinux-policy-targeted shadow-utils squashfs-tools tar unzip util-linux which yum

# wrapper-build
${PROG} add-group ${PRODUCT}-build wrapper-rpm-build
${PROG} add-group-pkg ${PRODUCT}-build wrapper-rpm-build bash redhat-release redhat-release-server redhat-rpm-config rpm-build shadow-utils

# Add required build packages
${PROG} add-pkg --owner=kojiadmin ${PRODUCT} bash binutils

# Adding Maven
if [ -d "/opt/kojak" ]; then
    ${PROG} import --create-build /opt/kojak/pkgs/${MAVEN}.src.rpm /opt/kojak/pkgs/${MAVEN}.noarch.rpm
    ${PROG} add-pkg --owner=kojiadmin ${PRODUCT} maven3
    ${PROG} tag-build ${PRODUCT}-candidate ${MAVEN}
else
    git clone https://github.com/sbadakhc/kojak.git ~/workspace/kojak
    ${PROG} import --create-build ~/workspace/kojak/pkgs/${MAVEN}.src.rpm ~/workspace/kojak/pkgs/${MAVEN}.noarch.rpm
    ${PROG} add-pkg --owner=kojiadmin ${PRODUCT} maven3
    ${PROG} tag-build ${PRODUCT}-candidate ${MAVEN}
fi
"

# Install Maven3
MVNRPM=$(rpm -q maven3)
if [ ! -z ${MVNRPM} ]; then
    echo -e "${MVNRPM} already installed"
else
    yum -y localinstall /opt/kojak/pkgs/maven3-3.0.3-4.noarch.rpm 
fi

