set term pdf monochrome dashed font "Times-Roman, 12"
set output "ss_hist_speedrate_100.pdf"
set style histogram clustered
set style fill pattern
set ytics 500
set style line 1 lt 4 lw 1
set style line 2 lt 1 lw 1
set grid ytics ls 1
set xlabel "Average Speed / Maximum Speed (%)"
set ylabel "Frequency"
set grid ytics ls 1
set xtics ("0" 0, "" 1, "10" 2, "" 3, "20" 4, "" 5, "30" 6, "" 7, "40" 8, "" 9, "50" 10, "" 11, "60" 12, "" 13, "70" 14, "" 15, "80" 16, "" 17, "90" 18) 
set key top left
#set title "ss_hist_speedrate_errorsm_100.gp"
plot "ss_hist_speedrate_errorsm_all_100.gpd" u 4 w histogram ls 2 t "All",\
	"ss_hist_speedrate_errorsm_tl_100.gpd" u 4 w histogram ls 2 t "Traffic Light",\
	"ss_hist_speedrate_errorsm_notl_100.gpd" u 4 w histogram ls 2 t "No Traffic Light"
