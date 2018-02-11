pdfName = paste(resultDir, "_init", sep = file.sep)
pdfName = paste(pdfName, "pdf", sep = ".")
print(pdfName)
pdf(pdfName, 7, 5)

par(mfrow = c(1, 2), oma = c(0,4.1,0,0), mar = c(3.4,0,0,1.3))  # 1 row and 2 columns

fmAlgo1 = fmTable[which(fmTable[2] == algos[1]),c(5,9,7,8,6)]
fmAlgo2 = fmTable[which(fmTable[2] == algos[2]),c(5)]
fmAlgo3 = fmTable[which(fmTable[2] == algos[3]),c(5)]
x  = as.vector(fmAlgo1[,2]) # 1:nrow(fmAlgo1)
x2 = as.vector(fmAlgo1[,2]) # + (1.05 * (max(fmAlgo1$V10) - min(fmAlgo1$V10))) # 1:nrow(fmAlgo1)
yOn1 = fmAlgo1[,1]
yOn2 = fmAlgo2
yOn3 = fmAlgo3
yOff1 = floor(as.numeric(fmAlgo1[,3]) / 10000) / 100
yOff2 = floor(as.numeric(fmAlgo1[,4]) / 10000) / 100
yOff3 = floor(as.numeric(fmAlgo1[,5]) / 10000) / 100

compareValue = 1 #yOn2

yOn1 = yOn1 / compareValue
yOn2 = yOn2 / compareValue
yOn3 = yOn3 / compareValue
yOff1 = yOff1 / compareValue
yOff2 = yOff2 / compareValue
yOff3 = yOff3 / compareValue

minX = min(x)
maxX = max(c(x,x2))
maxY = 2000 #max(c(yOn1,yOn2,yOn3,yOff1,yOff2,yOff3))
diffX = maxX - minX


plot(c(min(x), maxX), c(0, maxY), oma = c(0,0,0,0), mar = c(0,4.1,0,0),
     xaxt="n", 
     yaxt="n", 
     #xaxs="i", 
     #yaxs="i",
     type = "n", xlab="", ylab="")

axis(1, scaleAxis(minX, maxX, 50), labels=FALSE, tck=-0.01)
axis(1, scaleAxis(minX, maxX, 10), las=1)
axis(2, scaleAxis(0, maxY, 50), labels=FALSE, tck=-0.01)
axis(2, scaleAxis(0, maxY, 10), las=1)

legend(minX - 2, maxY + 40, 
       c("Sat-Based (SAT)",
         "Graph-Assisted (NMIG)", 
         "Graph-Assisted (CMIG)" 
       ), 
       cex = 0.9, title = " ", col = myColors[c(3,1,2)], pch = "*",
       bty = "n")

legend(minX + 10, maxY + 50, c("Offline Time"),
       cex = 0.9,
       text.font = 2,
       bty = "n")

title(mgp = c(2.5, 1, 0), xlab="Number of Features") 
mtext("Time in ms", 2, line = 3.2)

points(x2,yOff1, col=myColors[1], pch="*")
points(x2,yOff2, col=myColors[2], pch="*")
points(x2,yOff3, col=myColors[3], pch="*")

plot(c(min(x), maxX), c(0, maxY),
     xaxt="n", 
     yaxt="n", 
     #xaxs="i", 
     #yaxs="i",
     type = "n", xlab="", ylab="")

axis(1, scaleAxis(minX, maxX, 50), labels=FALSE, tck=-0.01)
axis(1, scaleAxis(minX, maxX, 10), las=1)

legend(minX - 2, maxY + 40, 
       c("Sat-Based (SAT)",
         "Graph-Assisted (NMIG)", 
         "Graph-Assisted (CMIG)" 
         ), 
       cex = 0.9, title = " ", col = myColors[c(3,1,2)], pch = "+", bty = "n")

legend(minX + 10, maxY + 50, c("Online Time"),
       cex = 0.9,
       text.font = 2,
       bty = "n")

title(mgp = c(2.5, 1, 0), xlab="Number of Features")

points(x,yOn2, col=myColors[2], pch="+")
points(x,yOn1, col=myColors[1], pch="+")
points(x,yOn3, col=myColors[3], pch="+")

#lines(x, fitted(loess(yOn1~x, dataset=yOn1, span=0.5)), col=myColors[1], lty=1, lwd=2)
#lines(x, fitted(loess(yOn2~x, dataset=yOn2, span=0.5)), col=myColors[2], lty=1, lwd=2)
#lines(x, fitted(loess(yOn3~x, dataset=yOn3, span=0.5)), col=myColors[3], lty=1, lwd=2)

dev.off()
