set term pdf monochrome dashed  font "Times-Roman, 12" 
set output "ss_speedrate_coverage_vcount_gt1.pdf"
set xlabel "Average Error (Km/h)"
set xtics 5
set ylabel "Coverage (%)" 
#set ytics 5
set style line 1 lt 3 lw 1
set style line 2 lw 2
set grid xtics ytics ls 1
set key bottom right
plot [][0:7] "ss_hist_speedrate_congestedcoverage_vcount_gt1_1.gpd" using 1:($2*100) smooth bezier with lines t "10%" linewidth 4,\
	"" u 1:($3*100) smooth bezier w lines t "20%" linewidth 4,\
	"" u 1:($4*100) smooth bezier w lines t "30%" linewidth 4,\
	"" u 1:($7*100) smooth bezier w lines t "60%" linewidth 4,\
	"" u 1:($11*100) smooth bezier w lines t "100%" linewidth 4
