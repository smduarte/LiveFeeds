set term pdf monochrome dashed font "Times-Roman, 12" 
#size 10cm, 6cm
#set size 0.3,0.3
#set size ratio 0.6
set output "ss_hist_sumoerror.pdf"
#set title "Average error relative to SUMO statistics"
set style line 1 lt 4 lw 1
set style line 2 lt 1 lw 1
set grid ytics ls 1
set xlabel "4Sensing Nodes (%)"
set ylabel "Average Error (Km/h)"
set style histogram clustered
set style fill solid
unset key
plot [][0:9]"ss_hist_speedrate_sumoerrorkmh_all.gpd" u 2:xtic(1) w histogram
