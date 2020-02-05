for i in $(find ./src/sensing/persistence/simsim/speedsense/ -name 'Script.groovy'); do cp $i $i.bck; sed 's/false/true/g' $i > $i.tmp; cp $i.tmp $i; done
