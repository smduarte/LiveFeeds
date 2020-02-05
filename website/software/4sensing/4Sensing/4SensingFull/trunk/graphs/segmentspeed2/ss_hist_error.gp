set term pdf monochrome
set output "ss_hist_error.pdf"
set title "ss_hist_error.gp" 
set style histogram clustered
set style fill pattern
set grid ytics
set xlabel "Nos 4Sensing (%)"
set ylabel "Erro medio (km/h)"
plot "ss_hist_speedrate_errorkmh_all.gpd" u 2:xtic(1) w histogram t "Media simples",\
	"ss_hist_speedrate_werrorkmh_all.gpd" u 2 w histogram t "Media ponderada",\
	"ss_hist_speedrate_errorkmh_vcount_gt1.gpd" u 2 w histogram t "Veiculos > 1 Media simples",\
	"ss_hist_speedrate_werrorkmh_vcount_gt1.gpd" u 2 w histogram t "Veiculos > 1 Media ponderada"
