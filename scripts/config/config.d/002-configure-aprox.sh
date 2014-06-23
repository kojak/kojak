#!/bin/sh

echo "Creating CI-* / CIx-* auto-proxy rule..."
test -d /opt/aprox/data/autoprox || mkdir -p /opt/aprox/data/autoprox

cat > /opt/aprox/data/autoprox/0010-ci.groovy << 'EOF'
import org.commonjava.aprox.autoprox.data.*;
import java.net.MalformedURLException;
import org.commonjava.aprox.model.*;

class CIRule extends AbstractAutoProxRule
{
    def REGEX = /CI([xX]?)-(.+)/

    boolean matches( String named )
    {
        return named =~ REGEX;
    }

    RemoteRepository createRemoteRepository( String named )
    {
        def match = (named =~ REGEX)[0]
        if ( match[2] == 'loop')
        {
            return null
        }

        RemoteRepository repo = new RemoteRepository( named, "http://koji.localdomain/kojifiles/repos/${match[2]}/latest/maven/" );
        repo.with {
            passthrough= true
            description= "Koji repository for tag: ${match[2]}"
        }

        return repo
    }

    HostedRepository createHostedRepository( String named )
    {
        HostedRepository repo = new HostedRepository( named );
        repo.with {
            allowSnapshots= true
            allowReleases=  true
            description= "CI snapshots for ${named}"
        }

        return repo
    }

    Group createGroup( String named )
    {
        Group g = new Group( named );

        def match = (named =~ REGEX)[0]

        def repoPart = "."
        if ( match[2] != 'loop')
        {
            g.addConstituent( new StoreKey( StoreType.remote, named ) )
            repoPart = ", along with a proxy to the Koji maven repository for the tag: '${match[2]}'."
        }

        def publicPart = ""
        if ( match[1] )
        {
            g.addConstituent( new StoreKey( StoreType.group, "public" ) )
            publicPart = " Also includes the constituents from the 'public' group."
        }

        g.addConstituent( new StoreKey( StoreType.hosted, named ) )

        g.setDescription( "Group containing a local CI snapshot/releases deployment repository${repoPart}${publicPart}" )

        g
    }

}
EOF

echo "Configuring public group to contain central and jboss.org public repository..."
test -d /opt/aprox/data/aprox/group || mkdir -p /opt/aprox/data/aprox/group

cat > /opt/aprox/data/aprox/group/public.json << 'EOF'
{"constituents":["remote:central","remote:JB-public"],"key":"group:public"}
EOF

echo "Setting up WebDAV mount..."
test -d /etc/davfs2 || mkdir /etc/davfs2

SETTINGS_SECRETS_LINE='/aprox/settings koji notused'
grep "$SETTINGS_SECRETS_LINE" /etc/davfs2/secrets > /dev/null || echo $SETTINGS_SECRETS_LINE >> /etc/davfs2/secrets

STORES_SECRETS_LINE='/aprox/stores koji notused'
grep "$STORES_SECRETS_LINE" /etc/davfs2/secrets > /dev/null || echo $STORES_SECRETS_LINE >> /etc/davfs2/secrets

mkdir -p /aprox/settings
mkdir -p /aprox/stores
chown -R koji:koji /aprox

SETTINGS_FSTAB_LINE='http://koji.localdomain/aprox/mavdav/settings    /aprox/settings    davfs    auto,ro    0 0'
grep "$SETTINGS_FSTAB_LINE" /etc/fstab > /dev/null || echo $SETTINGS_FSTAB_LINE >> /etc/fstab

STORES_FSTAB_LINE='http://koji.localdomain/aprox/mavdav/stores    /aprox/stores    davfs    auto,ro    0 0'
grep "$STORES_FSTAB_LINE" /etc/fstab > /dev/null || echo $STORES_FSTAB_LINE >> /etc/fstab

service aprox restart

echo "Sleeping 30s to allow AProx to start... (shouldn't take anything like that long)"
sleep 30
curl -I http://koji.localdomain:8090/aprox/mavdav/settings/group/settings-CIx-loop.xml

echo "Setting up reverse proxy to AProx..."
cat > /etc/httpd/conf.d/aprox.conf << 'EOF'
ProxyPreserveHost On
ProxyPass /aprox http://localhost:8090/aprox
ProxyPassReverse /aprox http://localhost:8090/aprox
EOF

APROX_CONF_INCLUDE="Include /etc/httpd/conf.d/aprox.conf"
grep "$APROX_CONF_INCLUDE" /etc/httpd/conf/httpd.conf || echo "$APROX_CONF_INCLUDE" >> /etc/httpd/conf/httpd.conf

service httpd graceful

mount /aprox/settings
mount /aprox/stores

for userdir in '/home/koji' '/root'; do
  user=$(basename $userdir)
  echo "Redirecting default Maven settings.xml for '${user}' to AProx...\n  ...using local-loop aprox deployment: '/aprox/settings/group/settings-CIx-loop.xml'"
  if [ ! -d $userdir/.m2 ]; then
    mkdir -p $userdir/.m2
    chown -R $user:$user $userdir/.m2
  fi

  KOJI_DEFAULT_SETTINGS_XML="$userdir/.m2/settings.xml"

  if [ -f $KOJI_DEFAULT_SETTINGS_XML ]; then
   mv $KOJI_DEFAULT_SETTINGS_XML ${KOJI_DEFAULT_SETTINGS_XML}.pre-aprox
  fi

  ln -s /aprox/settings/group/settings-CIx-loop.xml $KOJI_DEFAULT_SETTINGS_XML
done

echo "AProx configuration complete."

