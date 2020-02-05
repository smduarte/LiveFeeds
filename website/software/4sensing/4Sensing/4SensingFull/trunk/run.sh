#!/bin/bash
# run runid env times

SETUP_BASE="sensing.persistence.simsim.speedsense.sumo.setup.segmentspeed2"
SETUPS="SUMOTrafficSpeedSetup_1"
echo $SETUPS

if [ $# -ne 3 ]
then 
  echo "Usage: run.sh runid env num-times"
  exit 65
fi
if [ ! -d "logs" ]
then
  mkdir logs
fi
if [ ! -d "results" ]
then
  mkdir results
fi

RUN="$1_"`date "+%d-%m-%Y-%H-%M-%S"`
ENV="$2"
TIMES="$3"
. ./runenv_$ENV.sh

for item in "." "bin" lib/*.jar
do
  CLASSPATH=$CLASSPATH:$item
done

echo $CLASSPATH
#run name class server-settings
function run() {
  java $3 -classpath $CLASSPATH $2 $SETUP $RUN $ENV true  &> "logs/"$RUN"_"$1"_"$SETUP"_"$COUNTER".log" $4
}

  COUNTER=0
  while [ $COUNTER -lt $TIMES ]; do
	echo "run-$COUNTER"
	for i in $SETUPS; do
		SETUP="$SETUP_BASE.$i"
 	echo "SUMO Centralized" $RUN $SETUP $COUNTER
		run "SUMO_Centralized" sensing.persistence.simsim.speedsense.sumo.SUMOSpeedSenseSim  "$JVM_SETTINGS_CENT" $COUNTER


 #   	echo "QTree $RUN $SETUP $COUNTER"
 #		run QTree sensing.persistence.simsim.speedsense.osm.qtree.continuous.QTCSpeedSenseSim "$JVM_SETTINGS_QTREE" $COUNTER

#    echo "NTree" $COUNTER
#		 run NTree sensing.persistence.simsim.speedsense.osm.ntree.continuous.NTSpeedSenseSim "$JVM_SETTINGS_NTREE" $COUNTER

 #   	echo "RTree $RUN $SETUP $COUNTER" 
#			run RTree sensing.persistence.simsim.speedsense.osm.rndtree.continuous.RTSpeedSenseSim "$JVM_SETTINGS_RTREE" $COUNTER
  	done
        let COUNTER=COUNTER+1
  done
