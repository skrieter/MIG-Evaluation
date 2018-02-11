modelName = "linux_2_6_28_6"
filteredRawData = subset(table.raw.times, V1 == modelName & V3 %in% data.algorithms & startsWith(as.character(V2), "R"))

pdfName = paste(dir.result, modelName, sep = file.sep)
pdfName = paste(pdfName, "Time_ReducedSize", sep = "_")
pdfName = paste(pdfName, "pdf", sep = ".")
print(pdfName)
pdf(pdfName, 7, 5)
par(oma = c(0,0,0.3,0.22), mar = c(3.4,3.7,0,0))  # 1 row and 2 columns

lineTypes=c(5,1,3)

values = floor(as.numeric(filteredRawData[,6]) / 500000) / 2
maxX = max(filteredRawData[,8])
maxY = max(values[which(filteredRawData$V3==data.algorithms[1])])
plot(c(0,maxX), c(0, maxY),
     xaxt="n", 
     yaxt="n", 
     #axxs="i", 
     #yaxs="i",
     type = "n", ylab="", xlab="")
axis(1, scaleAxis(0, maxX, 50), labels=FALSE, tck=-0.01)
axis(1, scaleAxis(0, maxX, 10), las=1)
axis(2, scaleAxis(0, maxY, 50), labels=FALSE, tck=-0.01)
axis(2, scaleAxis(0, maxY, 10), las=1)

title(mgp = c(2.5, 1, 0), xlab="Number of Defined Features")
title(mgp = c(2.8, 1, 0), ylab="Time in ms") 

h = scaleAxis2(maxY)
for (i in 1:10) {
  lines(c(-250,maxX), c(h[i], h[i]), col=rgb(0,0,0,0.2), lty=1, lwd=1)
}
remove(h)

legend(3645, maxY - 1, 
       c("Sat-Based (ASAT)",
         "Graph-Assisted (IMIG)", 
         "Graph-Assisted (CMIG)"
       ), 
       cex = 0.9, title = " ", col = diagram.color[c(3,1,2)], lty = 1, lwd = 0, pch = 15,
       bty = "n")

legend(3000, maxY - 0, c("Exexution Time of Decision Propagation"),
       cex = 0.9,
       text.font = 2,
       bty = "n")

legend(3274, maxY - 41.5, 
       c("Sat-Based (SAT)",
         "Graph-Assisted (IMIG)", 
         "Graph-Assisted (CMIG)" 
       ), 
       cex = 0.9, title = " ", col = diagram.color[c(3,1,2)], lty = lineTypes[c(3,1,2)], lwd = 2.9,
       bty = "n",seg.len=3.6)

legend(3000, maxY - 40.5, c("Regression Curve for Mean Execution Time"),
       cex = 0.9,
       text.font = 2,
       bty = "n")

for (i in seq(along=data.algorithms)) {
  indexData = which(filteredRawData$V3==data.algorithms[i])
  
  scaleFactor = 15
  x = round((maxX - filteredRawData[indexData,8]) / scaleFactor) * scaleFactor
  y = values[indexData]
  co = (col2rgb(diagram.color[i])+1)/256
  
  df = data.frame(x,y)
  freq = as.vector(table(paste(df$x, df$y, sep = "_")))
  df = unique(df)
  
  points((df$x),(df$y), col=rgb(co[1],co[2],co[3], ((freq / max(freq)) * 0.9) + 0.1), pch=".")
}

for (i in 3:1) {
  indexData = which(filteredRawData$V3==data.algorithms[i])
  
  x = (maxX - filteredRawData[indexData,8])
  y = values[indexData]
  
  yA = aggregate(y, by=list(x), FUN=mean, na.rm=FALSE)
  x2 = yA[[1]]
  y2 = yA[[2]]
  
  co = (col2rgb(diagram.color[i])+1)/256
  
  #lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col="black", lty=lineTypes[i], lwd=3)
  lines(x2, fitted(loess(y2~x2, dataset=y2, span=diagram.regSpan)), col=rgb(0,0,0, 0.1), lwd=3)
  lines(x2, fitted(loess(y2~x2, dataset=y2, span=diagram.regSpan)), col=rgb(0,0,0, 0.8), lwd=3, lty=lineTypes[i])
  lines(x2, fitted(loess(y2~x2, dataset=y2, span=diagram.regSpan)), col=rgb(co[1],co[2],co[3], 0.7), lty=lineTypes[i], lwd=3)
}
remove(i)
remove(y)
remove(y)
remove(y)
remove(y)
remove(y)
remove(x)
remove(yA)
remove(x2)
remove(y2)
remove(co)
remove(indexData)
remove(scaleFactor)
remove(values)
remove(maxX)
remove(minX)
remove(maxY)
remove(df)
remove(freq)
remove(filteredRawData)
remove(pdfName)

dev.off()
