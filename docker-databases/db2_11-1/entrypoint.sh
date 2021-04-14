#!/bin/bash
#
#   Initialize DB2 instance in a Docker container
#
# # Authors:
#   * Leo (Zhong Yu) Wu       <leow@ca.ibm.com>
#   * Boris Manojlovic        <boris@steki.net>
#
# Copyright 2015, IBM Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

pid=0

function log_info {
 echo -e $(date '+%Y-%m-%d %T')"\e[1;32m $@\e[0m"
}

function log_error {
 echo -e >&2 $(date +"%Y-%m-%d %T")"\e[1;31m $@\e[0m"
}

function stop_db2 {
  log_info "stopping database engine"
  su - db2inst1 -c "db2stop force"
}

function start_db2 {
  log_info "starting database engine"
  su - db2inst1 -c "db2start"
}

function restart_db2 {
  # if you just need to restart db2 and not to kill this container
  # use docker kill -s USR1 <container name>
  kill ${spid}
  log_info "Asked for instance restart doing it..."
  stop_db2
  start_db2
  log_info "database instance restarted on request"
}

function terminate_db2 {
  kill ${spid}
  stop_db2
  if [ $pid -ne 0 ]; then
    kill -SIGTERM "$pid"
    wait "$pid"
  fi
  log_info "database engine stopped"
  exit 0 # finally exit main handler script
}

trap "terminate_db2"  SIGTERM
trap "restart_db2"   SIGUSR1

if [ ! -f ~/db2inst1_pw_set ]; then
  (echo "db2inst1-pwd"; echo "db2inst1-pwd") | passwd db2inst1 > /dev/null  2>&1
  if [ $? != 0 ];then
    log_error "Changing password for db2inst1 failed"
    exit 1
  fi
  touch ~/db2inst1_pw_set
fi

if [ ! -f ~/db2_license_accepted ];then
  if [ -z "$LICENSE" ];then
     log_error "error: LICENSE not set"
     log_error "Did you forget to add '-e LICENSE=accept' ?"
     exit 1
  fi

  if [ "${LICENSE}" != "accept" ];then
     log_error "error: LICENSE not set to 'accept'"
     log_error "Please set '-e LICENSE=accept' to accept License before use the DB2 software contained in this image."
     exit 1
  fi
  touch ~/db2_license_accepted
fi

if [[ $1 = "start" ]]; then
  log_info "Initializing container"
  start_db2
  log_info "Database db2diag log following"
  tail -f ~db2inst1/sqllib/db2dump/db2diag.log &
  export pid=${!}
  while true
  do
    sleep 10000 &
    export spid=${!}
    wait $spid
  done
else
  exec "$1"
fi
