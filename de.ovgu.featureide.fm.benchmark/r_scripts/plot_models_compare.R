rawData = raw_data[[modelIndex]]
filteredRawData = rawData[which(rawData[,3] %in% algos),]
filteredRawData = filteredRawData[which(startsWith(as.vector(filteredRawData[,2]), "R")),]

suffix="Compare"
pdfName = paste(resultDir, modelNames[modelIndex], sep = file.sep)
pdfName = paste(pdfName, suffix, "pdf", sep = ".")
print(pdfName)
pdf(pdfName, 7, 5)
par(oma = c(0,0,0,0), mar = c(3.4,3.7,0,0))  # 1 row and 2 columns

values = floor(as.numeric(filteredRawData[,6]) / 1) / 1000000
#values = floor(as.numeric(filteredRawData[,7]))



co = (col2rgb(myColors[1])+1)/256

indexData3 = which(filteredRawData$V3==algos[3]) # SAT
indexData1 = which(filteredRawData$V3==algos[1]) # NCIG
y3 = values[indexData3]
y1 = values[indexData1]

dataPoints = 40
xValues = filteredRawData[indexData,8]
scaleFactor = ceiling(max(xValues) / (dataPoints - 1))
x = round((max(xValues) - xValues) / scaleFactor) * scaleFactor
y = y3 - y1

df = data.frame(x,y)
#df = df[!y %in% boxplot.stats(y)$out,]
dat <- split(df$y, f = df$x)

#y = df$y



maxX = max(x) / scaleFactor
minY = min(y)
maxY = max(y)
yName = paste("Average", suffix)
plot(c(0,maxX + 2), scaleAxis(minY, maxY, 1),
     xaxt="n", 
     yaxt="n", 
     xaxs="i", 
     yaxs="i",
     type = "n", ylab="", xlab="")
axis(1, seq(1, dataPoints, 1), labels=FALSE, tck=-0.01)
axis(1, seq(dataPoints / 10, dataPoints, dataPoints / 10), labels=seq(10, 100, 10),las=1)
axis(2, scaleAxis(minY, maxY, 50), labels=FALSE, tck=-0.01)
axis(2, scaleAxis(minY, maxY, 10), las=1)

title(mgp = c(2.5, 1, 0), xlab="Number of Defined Features")
title(mgp = c(2.8, 1, 0), ylab="Time in ms") 

#h = scaleAxis2(maxY)
#for (i in 1:10) {
#  lines(c(0,maxX), c(h[i], h[i]), col=rgb(0,0,0,0.15), lty=1, lwd=1)
#}

legend(3550, maxY - 7.5, 
       c("Sat-Based",
         "Graph-Assisted (NCIG)", 
         "Graph-Assisted (CCIG)" 
       ), 
       cex = 0.9, title = " ", col = myColors[c(3,1,2)], lty = 1, lwd = 2,
       bty = "n")

legend(3200, maxY - 6.5, c("Average Time for Decision Propagation"),
       cex = 0.9,
       text.font = 2,
       bty = "n")

boxplot(dat, col=rgb(co[1],co[2],co[3], 1), add = TRUE, outline = TRUE, range = 0,
        xaxt="n", 
        yaxt="n", 
        xaxs="i", 
        yaxs="i", ylab="", xlab="")

dev.off()
print("done")
