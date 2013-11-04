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

set -x

## Declare environment varibles

# Distro information
VMNAME="Fedora-18-x86_64-DVD"
VMHOME="/var/lib/libvirt/images"
TMPDIR="/home/kojak"
DIST="RedHat/Fedora/18/0/x86_64"

# Sources and configuration
KSISO="Fedora-18-x86_64-DVD.iso"
KSCFG="Fedora-18-x86_64.cfg"

# Working directories
MNTDIR="${TMPDIR}/mnt/${DIST}"
CFGDIR="${TMPDIR}/cfg/${DIST}"
ISODIR="${TMPDIR}/iso/${DIST}"

# Virtual machine specifications
VMDISK="32768M"
VMMEM="4096"

mkdir -p $MNTDIR $OPTDIR $CFGDIR $BLDDIR $ISODIR $IMGDIR


if [ -e "$ISODIR/Fedora-18-x86_64-DVD.iso" ]; then
    echo "DVD ISO Found"
else
    echo "Downloading DVD ISO"
    cd $ISODIR
    wget http://download.fedoraproject.org/pub/fedora/linux/releases/18/Fedora/x86_64/iso/Fedora-18-x86_64-DVD.iso
    cd -
fi

cp kojak_ks.cfg $CFGDIR/

cd ${VMHOME}

# Remove any pre-existing vm with the same name
virsh destroy ${VMNAME}
virsh undefine ${VMNAME}

# Remove any pre-existing vm image with the same name
rm ${VMHOME}/${VMNAME}.img

# Allocate the diskspace for the vm
fallocate -l ${VMDISK} ${VMHOME}/${VMNAME}.img
chown qemu:qemu ${VMHOME}/${VMNAME}.img

# Create the vm with the following options
virt-install \
-n ${VMNAME} \
-r ${VMMEM} \
--vcpus=2 \
--os-type=linux \
--os-variant=fedora18 \
--accelerate \
--mac=00:00:00:00:00:00 \
--disk=${VMHOME}/${VMNAME}.img \
--disk=${ISODIR}/${KSISO},device=cdrom \
--location ${ISODIR}/${KSISO} \
--initrd-inject=${CFGDIR}/kojak_ks.cfg \
--extra-args="ks=file:kojak_ks.cfg console=tty0 console=ttyS0,115200 serial rd_NO_PLYMOUTH" \
--nographics

