#!/bin/bash
cd lib
tar -xmf apache-ant-1.8.1-bin.tar.gz;
cd ..
. ./env.sh
ant deploy
