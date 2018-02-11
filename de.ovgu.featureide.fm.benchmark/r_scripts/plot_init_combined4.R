pdfName = paste(dir.result, "_init5.3", sep = file.sep)
pdfName = paste(pdfName, "pdf", sep = ".")
print(pdfName)

pdf(pdfName, 14, 4.8)
layout(matrix(c(1,1,1,1,2,3,4,5), 2, 4, byrow = TRUE), heights = c(1,17), TRUE)
par(oma = c(0, 4.8, 0, 0))

#dev.off()
fs = 1.55
fs.l = 1.8

par(mar = c(0, 0, 0, 0))
plot.new()
legend(
  -0.03,
  2.45,
  c("Sat-Based (ASAT)"),
  cex = fs.l,
  title = " ",
  col = diagram.color[3],
  pch = 15,
  bty = "n",
  xpd = TRUE
)

legend(
  0.15,
  2.45,
  c("Graph-Assisted (IMIG)"),
  cex = fs.l,
  title = " ",
  col = diagram.color[1],
  pch = 17,
  bty = "n",
  xpd = TRUE
)

legend(
  0.364,
  2.45,
  c("Graph-Assisted (CMIG)"),
  cex = fs.l,
  title = " ",
  col = diagram.color[2],
  pch = 8,
  bty = "n",
  xpd = TRUE
)


fmAlgo1 = fmTable[fmTable[2] == data.algorithms[1], c(8, 12, 10, 11, 9)]
fmAlgo2 = fmTable[fmTable[2] == data.algorithms[2], c(8)]
fmAlgo3 = fmTable[fmTable[2] == data.algorithms[3], c(8)]
x  = as.vector(fmAlgo1[, 2]) # 1:nrow(fmAlgo1)
x2 = as.vector(fmAlgo1[, 2]) # + (1.05 * (max(fmAlgo1$V10) - min(fmAlgo1$V10))) # 1:nrow(fmAlgo1)
yOn1 = fmAlgo1[, 1]
yOn2 = fmAlgo2
yOn3 = fmAlgo3
yOff1 = floor(as.numeric(fmAlgo1[, 3]) / 10000) / 100  # IMIG
yOff2 = floor(as.numeric(fmAlgo1[, 4]) / 10000) / 100  # CMIG
yOff3 = floor(as.numeric(fmAlgo1[, 5]) / 10000) / 100  # ASAT

compareValue = 1 #yOn2

yOn1 = yOn1 / compareValue
yOn2 = yOn2 / compareValue
yOn3 = yOn3 / compareValue
yOff1 = yOff1 / compareValue
yOff2 = yOff2 / compareValue
yOff3 = yOff3 / compareValue

minX = min(x)
maxX = max(c(x, x2))
maxY = 1700 #max(c(yOn1+yOff1,yOn2+yOff2,yOn3+yOff3))
maxY = max(c(yOn1+yOff1,yOn2+yOff2,yOn3+yOff3))
diffX = maxX - minX

co1 = (col2rgb(diagram.color[1]) + 1) / 256
co2 = (col2rgb(diagram.color[2]) + 1) / 256
co3 = (col2rgb(diagram.color[3]) + 1) / 256

a = 0.7
c = 1.4

### ------------------------------------------- 111 ----------------------------------------- ###


par(mar = c(3.5, 0.5, 0, 0))
plot(
  c(min(x), maxX),
  c(0, maxY),
  xaxt = "n",
  yaxt = "n",
  #xaxs="i",
  #yaxs="i",
  type = "n",
  xlab = "",
  ylab = ""
)

axis(1, seq(1160, 1400, 8), labels = FALSE, tck = -0.01)
axis(1,
     c(1160, 1240, 1320, 1400),
     las = 1,
     cex.axis = fs)
axis(1, c(1200, 1280, 1360), las = 1, cex.axis = fs)
axis(2,
     scaleAxis(0, maxY, 50),
     labels = FALSE,
     tck = -0.01)
axis(2, scaleAxis(0, maxY, 10), las = 1, cex.axis = fs)

legend(
  minX - 19,
  1.03 * maxY,
  c("Offline Phase"),
  cex = fs,
  text.font = 2,
  bty = "n"
)

#title(mgp = c(2.5, 1, 0), xlab="Number of Features")
mtext("Time in ms", 2, line = 4, cex = 1.1)
mtext("Number of Features", 1, line = 2.6, cex = 1.1)

points(
  x2,
  yOff2,
  col = rgb(co2[1], co2[2], co2[3], a),
  pch = 8,
  cex = c
)
points(
  x2,
  yOff3,
  col = rgb(co3[1], co3[2], co3[3], a),
  pch = 15,
  cex = c
)
points(
  x2,
  yOff1,
  col = rgb(co1[1], co1[2], co1[3], a),
  pch = 17,
  cex = c
)



### ------------------------------------------- 222 ----------------------------------------- ###

plot(
  c(minX, maxX),
  c(0, maxY),
  xaxt = "n",
  yaxt = "n",
  #xaxs="i",
  #yaxs="i",
  type = "n",
  xlab = "",
  ylab = ""
)

axis(1, seq(1160, 1400, 8), labels = FALSE, tck = -0.01)
axis(1, c(1160, 1400), labels = FALSE)
axis(1, c(1200, 1280, 1360), las = 1, cex.axis = fs)
axis(1, c(1240, 1320), las = 1, cex.axis = fs)

legend(
  minX - 19,
  1.03 * maxY,
  c("Online Phase"),
  cex = fs,
  text.font = 2,
  bty = "n"
)

points(
  x,
  yOn2,
  col = rgb(co2[1], co2[2], co2[3], a),
  pch = 8,
  cex = c
)
points(
  x,
  yOn3,
  col = rgb(co3[1], co3[2], co3[3], a),
  pch = 15,
  cex = c
)
points(
  x,
  yOn1,
  col = rgb(co1[1], co1[2], co1[3], a),
  pch = 17,
  cex = c
)



### ------------------------------------------- 333 ----------------------------------------- ###

plot(
  c(min(x), maxX),
  c(0, maxY),
  type = "n",
  xaxt = "n",
  yaxt = "n",
  #xaxs="i",
  #yaxs="i",
  xlab = "",
  ylab = ""
)

axis(1, seq(1160, 1400, 8), labels = FALSE, tck = -0.01)
axis(1,
     c(1160, 1240, 1320, 1400),
     las = 1,
     cex.axis = fs)
axis(1, c(1200, 1280, 1360), las = 1, cex.axis = fs)

legend(
  minX - 19,
  1.03 * maxY,
  c("Offline + Online Phase"),
  cex = fs,
  text.font = 2,
  bty = "n"
)

points(
  x,
  (1 * yOn2) + yOff2,
  col = rgb(co2[1], co2[2], co2[3], a),
  pch = 8,
  cex = c
)
points(
  x,
  (1 * yOn3) + yOff3,
  col = rgb(co3[1], co3[2], co3[3], a),
  pch = 15,
  cex = c
)
points(
  x,
  (1 * yOn1) + yOff1,
  col = rgb(co1[1], co1[2], co1[3], a),
  pch = 17,
  cex = c
)

### ------------------------------------------- 444 ----------------------------------------- ###

# 1 = IMIG
# 2 = CMIG
# 3 = ASAT

xyz31 = (yOff3 - yOff1) / (yOn1 - yOn3) # CMIG <=> IMIG
xyz32 = (yOff3 - yOff2) / (yOn2 - yOn3) # CMIG <=> ASAT
xyz12 = (yOff1 - yOff2) / (yOn2 - yOn1) # IMIG <=> ASAT

b = data.frame(xyz31, xyz32, xyz12)

par(mar = c(3.5, 6.2, 0, 0))

r = c(0, 0.3)
boxplot(
  b,
  xaxt = "n",
  yaxt = "n",
  #type = "n",
  xlab = "",
  ylab = "",
  ylim = c(0.1, 300),
  log="y"
)
axis(2,
     c(seq(0.1,0.9,0.1),seq(1,9,1),seq(10,90,10),seq(100,300,100)),
     labels = FALSE,
     tck = -0.015)
axis(2,
     c(0.1, 1, 10, 100),
     las = 1,
     #tck = -0.04,
     cex.axis = fs)
axis(2,
     c(0.2, 0.5, 2, 5, 20, 50, 200),
     las = 1,
     tck = -0.015,
     cex.axis = fs)
axis(1,
     c(1,2,3),
     labels = FALSE,
     tck = -0.015)

mtext("Number of Online Phases", 2, line = 4.5, cex = 1.1)

legend(
  0.36,
  0.081, #1.1,
  c(" IMIG", "ASAT"),
  text.col = c (diagram.color[1], diagram.color[3]),
  cex = fs,
  text.font = 2,
  bty = "n",
  xpd = TRUE
)

legend(
  1.36,
  0.081, #11,
  c("CMIG", "ASAT"),
  text.col = c (diagram.color[2], diagram.color[3]),
  cex = fs,
  text.font = 2,
  bty = "n",
  xpd = TRUE
)

legend(
  2.36,
  0.081, #100,
  c("CMIG", " IMIG"),
  text.col = c (diagram.color[2], diagram.color[1]),
  cex = fs,
  text.font = 2,
  bty = "n",
  xpd = TRUE
)

legend(
  0.2,
  400,
  c("Break-even point"),
  cex = fs,
  text.font = 2,
  bty = "n",
  xpd = TRUE
)

#mtext("Number of Online Phases", 1, line = 2.6, cex = 1.1)

dev.off()

cat(min(xyz31))
cat("--")
cat(max(xyz31))
cat(" median:")
cat(median(xyz31))
cat("\n")


cat(min(xyz32))
cat("--")
cat(max(xyz32))
cat(" median:")
cat(median(xyz32))
cat("\n")


cat(min(xyz12))
cat("--")
cat(max(xyz12))
cat(" median:")
cat(median(xyz12))
cat("\n")

remove(pdfName)
