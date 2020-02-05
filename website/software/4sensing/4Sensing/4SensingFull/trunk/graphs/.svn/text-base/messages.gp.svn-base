set boxwidth 0.75 absolute
set style fill solid 1.00 border -1
set style histogram columnstacked
set style data histograms
plot "run_baseline_50k_RTSpeedSenseSim_HSSetup2_50kNodes_5kfixed_messages.gpd" using ($7/15) title "RTree",\
	"run_baseline_50k_QTCSpeedSenseSim_HSSetup2_50kNodes_5kfixed_messages.gpd" using ($7/15) title "QTree",\
	"run_baseline_50k_NTSpeedSenseSim_HSSetup2_50kNodes_5kfixed_messages.gpd" using ($7/15)  title "NTree",\
	"run_baseline_50k_CSpeedSenseSim_HSSetup2_50kNodes_5kfixed_messages.gpd" using ($7/15)  title "Cent"
