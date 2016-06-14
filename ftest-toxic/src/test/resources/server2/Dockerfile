FROM centos:7

RUN yum install -y nmap-ncat

EXPOSE 80
CMD nc -lk 0.0.0.0 80 -c 'curl -D - http://google.com'