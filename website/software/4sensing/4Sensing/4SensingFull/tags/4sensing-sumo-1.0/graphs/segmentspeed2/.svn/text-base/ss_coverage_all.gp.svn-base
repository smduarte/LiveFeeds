set term pdf monochrome dashed font "Times-Roman, 12"
set output "ss_coverage_all.pdf"
#set title "ss_coverage.gp"
set style histogram clustered
set style fill pattern
set ytics 10
set style line 1 lt 4 lw 1
set style line 2 lt 1 lw 1
set grid ytics ls 1
set xlabel "Average Speed / Maximum Speed (%)"
set ylabel "Coverage (%)"
set key outside right
plot [][0:100]"ss_hist_speedrate_congestedcoverage_1_overall.gpd" u ($2*100):xtic(1) w histogram ls 2 t "1%",\
	"ss_hist_speedrate_congestedcoverage_2_overall.gpd" u ($2*100) w histogram ls 2 t "2%",\
	"ss_hist_speedrate_congestedcoverage_3_overall.gpd" u ($2*100) w histogram ls 2 t "3%",\
	"ss_hist_speedrate_congestedcoverage_5_overall.gpd" u ($2*100) w histogram ls 2 t "5%",\
	"ss_hist_speedrate_congestedcoverage_7_overall.gpd" u ($2*100) w histogram ls 2 t "7%",\
	"ss_hist_speedrate_congestedcoverage_10_overall.gpd" u ($2*100) w histogram ls 2 t "10%"
