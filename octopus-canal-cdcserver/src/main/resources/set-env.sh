if [ -z "$DOCKER_HOST_IP" ] ; then
    if [ -z "$DOCKER_HOST" ] ; then
      export DOCKER_HOST_IP=`hostname`
    else
      echo using ${DOCKER_HOST?}
      XX=${DOCKER_HOST%\:*}
      export DOCKER_HOST_IP=${XX#tcp\:\/\/}
    fi
fi

export CANAL_DESTINATIONS=test
export CANAL_FILTER_REGEX=*\\.global_events

echo DOCKER_HOST_IP is $DOCKER_HOST_IP
echo CANAL_DESTINATIONS is $CANAL_DESTINATIONS
echo CANAL_FILTER_REGEX is $CANAL_FILTER_REGEX
