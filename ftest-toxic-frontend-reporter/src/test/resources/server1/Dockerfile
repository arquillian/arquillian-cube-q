FROM centos:7

RUN yum install -y nmap-ncat

EXPOSE 8085
CMD nc -lk 0.0.0.0 8085 -c 'curl -D - http://server2'