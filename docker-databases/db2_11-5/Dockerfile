#  Build docker image of db2 express-C v10.5 FP5 (64bit)
#
# # Authors:
#   * Leo (Zhong Yu) Wu       <leow@ca.ibm.com>
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

FROM centos:8 AS base

###############################################################
#
#               System preparation for DB2
#
###############################################################

RUN groupadd db2iadm1 && useradd -G db2iadm1 db2inst1

# Required packages
RUN yum install -y \
    vi \
    sudo \
    passwd \
    pam \
    pam.i686 \
    ncurses-libs.i686 \
    file \
    libaio \
    libstdc++-devel.i686 \
    numactl-libs \
    which \
    glibc-locale-source \
    glibc-langpack-de \
    && yum clean all

RUN localedef -i de_DE -c -f UTF-8 \
    -A /usr/share/locale/locale.alias de_DE.UTF-8 && echo "LANG=de_DE.UTF-8" > /etc/locale.conf
RUN echo "export LANG=de_DE.UTF-8" >> /etc/profile

FROM base AS db2-install

COPY ./db.tar.gz /tmp/expc.tar.gz
COPY ./db2server.rsp /tmp/db2server.rsp

RUN cd /tmp && tar xf expc.tar.gz \
    && su - db2inst1 -c "/tmp/server_dec/db2setup -r /tmp/db2server.rsp" \
    && echo '. /home/db2inst1/sqllib/db2profile' >> /home/db2inst1/.bash_profile \
    && rm -rf /tmp/db2* && rm -rf /tmp/expc* && rm -rf /tmp/server_dec \
    && sed -ri  's/(ENABLE_OS_AUTHENTICATION=).*/\1YES/g' /home/db2inst1/sqllib/instance/db2rfe.cfg \
    && sed -ri  's/(RESERVE_REMOTE_CONNECTION=).*/\1YES/g' /home/db2inst1/sqllib/instance/db2rfe.cfg \
    && sed -ri 's/^\*(SVCENAME=db2c_db2inst1)/\1/g' /home/db2inst1/sqllib/instance/db2rfe.cfg \
    && sed -ri 's/^\*(SVCEPORT)=48000/\1=50000/g' /home/db2inst1/sqllib/instance/db2rfe.cfg

RUN echo "0 localhost 0" > /home/db2inst1/sqllib/db2nodes.cfg

FROM base

COPY --from=db2-install /home/ /home/

RUN su - db2inst1 -c "db2start && db2set DB2COMM=TCPIP \
      && db2 create database TSKDB using codeset utf-8 territory en-us \
      collate using 'CLDR181_LDE_AS_CX_EX_FX_HX_NX_S3' PAGESIZE 32 K" \
    && su - db2inst1 -c "db2stop force" \
    && cd /home/db2inst1/sqllib/instance \
    && ./db2rfe -f ./db2rfe.cfg

COPY entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
CMD ["start"]

EXPOSE 50000
