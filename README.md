ethlo Bucket Store
==================

Minimalist S3 compatible bucket storage (No ACL, logging, attributes, etc) with pluggable back-end using [ethlo KV API](http://github.com/ethlo/kvapi).

Please see the KV API page for more information on storage implementations available.

Build status
============
[![Build Status](https://travis-ci.org/ethlo/ebs-server.png)](https://travis-ci.org/ethlo/ebs-server)

How to run
==========
```
java -jar ebs-server.jar /etc/ebs/ebs.xml
```

Install as linux service (.deb)
===============================
sudo dpkg -i ebs-server-1.0.deb
