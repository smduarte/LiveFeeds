def diff = {a, b -> Math.abs(b-a)}

println diff(10, 15);
println diff(Double.NaN, 15) > 1;
println diff(Double.NaN, Double.NaN) > 1;

println Double.NaN == Double.NaN;
println Double.NaN != Double.NaN;
