readCSVs <- function(fileNames) {
  list.raw.data <- vector('list', length(fileNames))
  for (i in seq(along = fileNames)) {
    print(fileNames[i])
    list.raw.data[[i]] <- tryCatch({
      if (file.size(fileNames[i]) > 0){
        read.csv(fileNames[i], sep = ";", header = FALSE)
      } else {
        data.frame()
      }
    }, error = function(err) {
      return(data.frame())
    })
  }
  return(do.call('rbind', list.raw.data))
}

scaleAxis1 <- function(maxArg, steps) {
  yt = floor(log10(maxArg))
  ym = ceiling(maxArg / 10^yt) * (10^yt)
  
  return(seq(0, ym, (ym / steps)))
}
scaleAxis2 <- function(maxArg) {
  yt = floor(log10(maxArg))
  ym = ceiling(maxArg / 10^yt) * (10^yt)
  
  return(seq(0, ym, (ym / 10)))
}
scaleAxis <- function(minArg, maxArg, steps) {
  yt = floor(log10(maxArg - minArg))
  ymax = ceiling(maxArg / 10^yt) * (10^yt)
  ymin = floor(minArg / 10^yt) * (10^yt)
  
  return(seq(ymin, ymax, ((ymax - ymin) / steps)))
}

plotModelDataPart <- function(maxX, values, filteredRawData, pdfName, suffix) {
  pdfName2 = paste(pdfName, suffix, "pdf", sep = ".")
  print(pdfName2)
  pdf(pdfName2, 16, 9)
  
  maxY = max(values[which(filteredRawData$V3==algos[1])])
  yName = paste("Average", suffix)
  plot(c(0,maxX), c(0, maxY),
       yaxt="n", 
       xaxs="i", 
       yaxs="i",
       type = "n", xlab="Step in Configuration Process", ylab=yName)
  axis(2, seq(0, maxY, maxY / 50), labels=FALSE)
  axis(2, seq(0, maxY, maxY / 10), las=1)
  for (i in 1:10) {
    lines(c(0,maxX), c(i * (maxY / 10), i * (maxY / 10)), col=rgb(0,0,0,0.3), lty=1, lwd=1)
  }
  
  for (i in seq(along=algos)) {
    indexData = which(filteredRawData$V3==algos[i])
    
    x = maxX - filteredRawData[indexData,8]
    #x = maxX - filteredRawData[indexData,8]
    y = values[indexData]
    co = (col2rgb(diagram.color[i])+1)/256
    points(x, y, col=rgb(co[1],co[2],co[3],alpha), pch=".")
    
    yA = aggregate(y, by=list(x), FUN=mean, na.rm=FALSE)
    #yA = aggregate(y, by=list(x), FUN=median, na.rm=FALSE)
    x2 = yA[[1]]
    y2 = yA[[2]]
    
    lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col="black", lty=1, lwd=3)
    lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col=diagram.color[i], lty=1, lwd=2.5)
    #lines(x2, y2, col="black", lty=1, lwd=2)
    #lines(x2, y2, col=diagram.color[i], lty=1, lwd=1)
  }
  dev.off()
}

plotModelDataPart2 <- function(maxX, values, filteredRawData, pdfName, suffix) {
  pdfName2 = paste(pdfName, suffix, "pdf", sep = ".")
  print(pdfName2)
  pdf(pdfName2, 16, 9)
  
  maxY = max(values[which(filteredRawData$V3==algos[1])])
  yName = paste("Average", suffix)
  plot(c(0,log(maxX)), c(0, maxY),
       yaxt="n", 
       xaxs="i", 
       yaxs="i",
       type = "n", xlab="Step in Configuration Process", ylab=yName)
  axis(2, seq(0, maxY, maxY / 50), labels=FALSE)
  axis(2, seq(0, maxY, maxY / 10), las=1)
  for (i in 1:10) {
    lines(c(0,log(maxX)), c(i * (maxY / 10), i * (maxY / 10)), col=rgb(0,0,0,0.3), lty=1, lwd=1)
  }
  
  for (i in seq(along=algos)) {
    indexData = which(filteredRawData$V3==algos[i])
    
    x = log(maxX - filteredRawData[indexData,8] + 1)
    #x = maxX - filteredRawData[indexData,8]
    y = values[indexData]
    co = (col2rgb(diagram.color[i])+1)/256
    points(x, y, col=rgb(co[1],co[2],co[3],alpha), pch=".")
    
    #yA = aggregate(y, by=list(x), FUN=mean, na.rm=FALSE)
    yA = aggregate(y, by=list(x), FUN=median, na.rm=FALSE)
    x2 = yA[[1]]
    y2 = yA[[2]]
    
    #lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col="black", lty=1, lwd=3)
    #lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col=diagram.color[i], lty=1, lwd=2.6)
    lines(x2, y2, col="black", lty=1, lwd=3)
    lines(x2, y2, col=diagram.color[i], lty=1, lwd=2.6)
  }
  dev.off()
}

plotModelDataPart3 <- function(maxX, values, filteredRawData) {
  for (i in seq(along=algos)) {
    indexData = which(filteredRawData$V3==algos[i])
    
    x = (maxX - filteredRawData[indexData,8]) / maxX * 100
    y = values[indexData]
    
    yA = aggregate(y, by=list(x), FUN=mean, na.rm=FALSE)
    #yA = aggregate(y, by=list(x), FUN=median, na.rm=FALSE)
    x2 = yA[[1]]
    y2 = yA[[2]]
    lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col=diagram.color[i], lty=1, lwd=1)
    #lines(x2, y2, col=diagram.color[i], lty=2, lwd=2)
  }
}

plotModelPartData4 <- function(maxX, values, filteredRawData) {
   # Plot
  #indexData2 = which(filteredRawData$V3==algos[2] & filteredRawData$V2==levels(filteredRawData$V2)[3])
  #indexData3 = which(filteredRawData$V3==algos[3] & filteredRawData$V2==levels(filteredRawData$V2)[3])
  indexData2 = which(filteredRawData$V3==algos[1])
  indexData3 = which(filteredRawData$V3==algos[2])
    sat = rawTime[indexData3]
    fg = rawTime[indexData2]
    
    x = (maxX - filteredRawData[indexData2,8]) / maxX * 100
    #y = ((sat - fg) / (max(sat - fg))) * 100
    y =  ((sat - fg) / compareTime) * 100
    z =  (sat / fg) 
    
    
    #lines(x, y, col=diagram.color[3], lty=1, lwd=2)
    #lines(x, z, col=diagram.color[1], lty=2, lwd=2)
    
    yA = aggregate(y, by=list(x), FUN=mean, na.rm=FALSE)
    zA = aggregate(z, by=list(x), FUN=mean, na.rm=FALSE)
    zASat = aggregate(sat, by=list(x), FUN=mean, na.rm=FALSE)
    zAFG = aggregate(fg, by=list(x), FUN=mean, na.rm=FALSE)
    ##yA = aggregate(y, by=list(x), FUN=median, na.rm=FALSE)
    x2 = yA[[1]]
    y2 = yA[[2]]
    #z2 = zA[[2]]
    z2 = (zASat[[2]] / zAFG[[2]]) 
    ##lines(x2, fitted(loess(y2~x2, dataset=y2, span=regSpan)), col=diagram.color[3], lty=1, lwd=1)
    lines(log(x2), y2, col=diagram.color[1], lty=1, lwd=2)
    #lines(log(x2), z2, col=diagram.color[1], lty=2, lwd=2)
}
