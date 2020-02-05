#!/bin/bash
cat /dev/null > $1/results.csv; for f in `find $1 -name 'Workload_Input_All_Nodes.csv'`; do echo -n "$f," >> $1/results.csv; echo $((`grep '^[0-9]' $f | wc -l`-1)) >> $1/results.csv; done;
