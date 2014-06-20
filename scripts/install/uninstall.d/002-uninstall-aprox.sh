#!/bin/sh

echo "Unmounting all /aprox/* filesystems..."
for d in $(ls -1d /aprox); do
  umount $d
done

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

