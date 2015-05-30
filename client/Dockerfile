FROM ubuntu:latest

# install pip and parallel
RUN apt-get update && \
    apt-get install -y --no-install-recommends python3-pip parallel && \
    apt-get clean && \
    rm -rf /var/cache/* /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN mkdir /usr/src/app

ADD run.sh /usr/src/app/
ADD requirements.txt /usr/src/app/
ADD *.py  /usr/src/app/

# install dependencies
RUN pip3 install -r /usr/src/app/requirements.txt

CMD ["/usr/src/app/run.sh"]
