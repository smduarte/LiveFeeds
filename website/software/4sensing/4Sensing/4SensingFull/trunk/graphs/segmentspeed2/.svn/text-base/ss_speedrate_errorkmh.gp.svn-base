set term pdf monochrome dashed font "Times-Roman, 12"
set output "ss_speedrate_errorkmh.pdf"
set style histogram clustered
set style fill pattern
set xtics ("0" 0, "" 1, "10" 2, "" 3, "20" 4, "" 5, "30" 6, "" 7, "40" 8, "" 9, "50" 10, "" 11, "60" 12, "" 13, "70" 14, "" 15, "80" 16, "" 17, "90" 18)
set ytics 5
set style line 1 lt 4 lw 1
set style line 2 lt 1 lw 1
set grid ytics ls 1
set xlabel "Average Speed / Maximum Speed (%)"
set ylabel "Average Error (Km/h)"
#set key outside right
plot [][0:20] "ss_hist_speedrate_errorkmh_all_1.gpd" u 2 w histogram ls 2 t "1%",\
	"ss_hist_speedrate_errorkmh_all_2.gpd" u 2 w histogram ls 2 t "2%",\
	"ss_hist_speedrate_errorkmh_all_3.gpd" u 2 w histogram ls 2 t "3%",\
	"ss_hist_speedrate_errorkmh_all_5.gpd" u 2 w histogram ls 2 t "5%",\
	"ss_hist_speedrate_errorkmh_all_7.gpd" u 2 w histogram ls 2 t "7%",\
	"ss_hist_speedrate_errorkmh_all_10.gpd" u 2 w histogram ls 2 t "10%"
