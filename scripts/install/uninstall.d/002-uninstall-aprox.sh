#!/bin/sh

echo "Unmounting all /aprox/* filesystems..."
umount /aprox/settings/

echo "Removing aprox mounts from /etc/fstab..."
mv /etc/fstab /etc/fstab.aprox
sed '/aprox/d' /etc/fstab.aprox > /etc/fstab
rm /etc/fstab.aprox

echo "Removing .m2 directory from koji home directory..."
rm -rf /home/koji/.m2

echo "Removing AProx install and mountpoints..."
rm -rf /aprox
rm -rf /opt/aprox

echo "Removing autofs, davfs2, and rpmforge-release via Yum..."
yum -y remove rpmforge-release davfs2

echo "AProx uninstall complete."

