#!/bin/sh

echo "Unmounting all /aprox/* filesystems..."
umount /aprox/settings/

echo "Returning fstab to original state..."
cat /etc/fstab.orig > /etc/fstab

echo "Removing aprox autofs configuration..."
rm /etc/auto.aprox

echo "Removing .m2 directory from koji home directory..."
rm -rf /home/koji/.m2

echo "Removing AProx install and mountpoints..."
rm -rf /aprox
rm -rf /opt/aprox

echo "Removing autofs, davfs2, and rpmforge-release via Yum..."
yum -y remove rpmforge-release davfs2 autofs

echo "AProx uninstall complete."

