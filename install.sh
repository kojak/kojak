#!/bin/bash
# 
# Copyright (C) 2013 Red Hat Inc.
# Author: Salim Badakhchani <sal@redhat.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.	
#
########################################################################

# Uncomment to debug script
#set -x

# Execute menu configuration options
while true
do

if [ "$USER" != "root" ]; then
    echo "This script requires root priviledges to run"
    exit
fi

## Declare environment varibles
# Distro information
VMNAME=${VMNAME:="Fedora-18-x86_64-DVD"}
VMHOME=${VMHOME:="/var/lib/libvirt/images"}
TMPDIR=${TMPDIR:="/home/kojak"}
DIST=${DIST:="RedHat/Fedora/18/0/x86_64"}

# Sources and configuration
KSISO=${KSISO:="Fedora-18-x86_64-DVD.iso"}
KSCFG=${KSCFG:="Fedora-18-x86_64.cfg"}

# Working directories
CFGDIR="${TMPDIR}/cfg/${DIST}"
ISODIR="${TMPDIR}/iso/${DIST}"

# Virtual machine specifications
VMDISK="32768M"
VMMEM="4096"

# Run Kojak configurator
clear
echo -e "\n################################################################################"
echo -e "#                                                                              #"
echo -e "#               !!! Welcom to the Kojak configuration menu !!!                 #"
echo -e "#                                                                              #"
echo -e "#           *** Press return to accept the default menu options ***            #\n"

# Virtual machine name
read -e -i "$vmname" -p "# VMNAME = $VMNAME : " vmname
vmname="${vmname:=$VMNAME}"

# Images directory
read -e -i "$vmhome" -p "# VMHOME = $VMHOME : " vmhome
vmhome="${vmhome:=$VMHOME}"

# Temp directory
read -e -i "$tmpdir" -p "# TMPDIR = $TMPDIR : " tmpdir
tmpdir="${tmpdir:=$TMPDIR}"

# Distribution directory
read -e -i "$dist" -p "# DIST = $DIST: " dist
dist="${dist:=$DIST}"

# Kickstart iso file
read -e -i "$ksiso" -p "# KSISO = $KSISO: " ksiso
ksiso="${ksiso:=$KSISO}"

# Kickstart configuration
read -e -i "$kscfg" -p "# KSCFG is $KSCFG: " kscfg
kscfg="${kscfg:=$KSCFG}"

# Kojak diirectories
read -e -i "$cfgdir" -p "# CFGDIR is $CFGDIR: " cfgdir
cfgdir="${tmpdir}/cfg/${dist}"
read -e -i "$isodir" -p "# ISODIR is $ISODIR: " isodir
isodir="${tmpdir}/iso/${dist}"

# Virtual machine specifications
read -e -i "$vmdisk" -p "# VMDISK is $VMDISK: " vmdisk
vmdisk="32768M"
read -e -i "$vmmem" -p "# VMMEM is $VMMEM: " vmmem
vmmem="4096"


echo -e "\n##   Please check the configuration options and accept if they are correct   ##\n"
echo -e "# VMNAME=$vmname"
echo -e "# VMHOME=$vmhome"
echo -e "# TMPDIR=$tmpdir"
echo -e "# DIST=$dist"
echo -e "# KSISO=$ksiso"
echo -e "# KSCFG=$kscfg"
echo -e "# CFGDIR=$cfgdir"
echo -e "# ISODIR=$isodir"
echo -e "# VMDISK=$vmdisk"
echo -e "# VMMEM=$vmmem"

echo -e "\n"
read -s -n1 -p "Are these options correct? Press y/n: " KEY
case "$KEY" in
 y ) echo -e "\n\nUsing supplied configuration options." && break;;
 n ) echo -e "\n\nRestarting configurator."  && sleep 1;;
 * ) echo -e "\n\nERROR: Incorrect user input...Please press y or n" && sleep 1;;
esac
done

mkdir -p $cfgdir $isodir 


if [ -f "$isodir/Fedora-18-x86_64-DVD.iso" ]; then
    echo "DVD ISO Found"
else
    echo "Downloading DVD ISO"
    cd $isodir
    wget http://download.fedoraproject.org/pub/fedora/linux/releases/18/Fedora/x86_64/iso/Fedora-18-x86_64-DVD.iso
    cd -
fi

cp kojak_ks.cfg $cfgdir/

cd ${vmhome}

# Remove any pre-existing vm with the same name
virsh destroy ${vmname}
virsh undefine ${vmname}

# Remove any pre-existing vm image with the same name
rm ${vmhome}/${vmname}.img

# Allocate the diskspace for the vm
fallocate -l ${vmdisk} ${vmhome}/${vmname}.img
chown qemu:qemu ${vmhome}/${vmname}.img

# Create the vm with the following options
virt-install \
-n ${vmname} \
-r ${vmmem} \
--vcpus=2 \
--os-type=linux \
--os-variant=fedora18 \
--accelerate \
--mac=00:00:00:00:00:00 \
--disk=${vmhome}/${vmname}.img \
--disk=${isodir}/${ksiso},device=cdrom \
--location ${isodir}/${ksiso} \
--initrd-inject=${cfgdir}/kojak_ks.cfg \
--extra-args="ks=file:kojak_ks.cfg console=tty0 console=ttyS0,115200 serial rd_NO_PLYMOUTH" \
--nographics

