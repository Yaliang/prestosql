#!/bin/bash

set -euo pipefail -x

. ${BASH_SOURCE%/*}/common.sh

cleanup_docker_containers
start_docker_containers



# insert AWS credentials
exec_in_hadoop_master_container cp /etc/hadoop/conf/core-site.xml.s3-template /etc/hadoop/conf/core-site.xml
exec_in_hadoop_master_container sed -i \
  -e "s|%AWS_ACCESS_KEY%|${AWS_ACCESS_KEY_ID}|g" \
  -e "s|%AWS_SECRET_KEY%|${AWS_SECRET_ACCESS_KEY}|g" \
  -e "s|%S3_BUCKET_ENDPOINT%|${S3_BUCKET_ENDPOINT}|g" \
 /etc/hadoop/conf/core-site.xml

# download jsonserde
exec_in_hadoop_master_container wget http://central.maven.org/maven2/org/apache/hive/hcatalog/hive-hcatalog-core/1.2.1/hive-hcatalog-core-1.2.1.jar -P /usr/hdp/2.6.3.0-235/hive/lib

stop_unnecessary_hadoop_services

# restart hive-metastore to apply S3 changes in core-site.xml
docker exec $(hadoop_master_container) supervisorctl restart hive-metastore
docker exec $(hadoop_master_container) supervisorctl restart hive-server2

HADOOP_MASTER_CONTAINER=$(hadoop_master_container)
echo ${HADOOP_MASTER_CONTAINER}

## run product tests
#pushd ${PROJECT_ROOT}
#set +e
#./mvnw -B -pl presto-hive-hadoop2 test -P test-hive-hadoop2 \
#  -Dhive.hadoop2.timeZone=UTC \
#  -DHADOOP_USER_NAME=hive \
#  -Dhive.hadoop2.metastoreHost=localhost \
#  -Dhive.hadoop2.metastorePort=9083 \
#  -Dhive.hadoop2.databaseName=default \
#  -Dhive.hadoop2.metastoreHost=hadoop-master \
#  -Dhive.hadoop2.timeZone=Asia/Kathmandu \
#  -Dhive.metastore.thrift.client.socks-proxy=${PROXY}:1180 \
#  -Dhadoop-master-ip=${HADOOP_MASTER_IP}
#EXIT_CODE=$?
#set -e
#popd

#cleanup_docker_containers
#
#exit ${EXIT_CODE}
