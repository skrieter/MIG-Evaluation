text = ""

mean01 = vector()
mean02 = vector()
mean02_ = vector()
mean03 = vector()
mean04 = vector()
mean05 = vector()
mean06 = vector()
mean07 = vector()
mean08 = vector()
mean09 = vector()
mean10 = vector()
mean11 = vector()
mean12 = vector()
mean13 = vector()
mean14 = vector()

sortedModelNames = as.vector(unique(table.raw.fmStatistics[order(table.raw.fmStatistics$V2),]$V1))
prefilteredRawData = subset(table.raw.times, V3 %in% data.algorithms & startsWith(as.character(V2), "R"))

for (i in seq(along=sortedModelNames)) {
  modelName = sortedModelNames[i]
  cat(length(sortedModelNames) - i)
  cat(" - ")
  cat(modelName)
  cat("\n")
  
  # Data
  stat = table.fm.org[table.fm.org$V1==modelName,]
  filteredRawData = subset(prefilteredRawData, V1 == modelName)
  
  maxDef = max(filteredRawData$V8)
  rawTime = floor(as.numeric(filteredRawData[,6]) / 10000) / 100
  
  d = filteredRawData[which(filteredRawData$V8 >= maxDef*0.97),]
  sumsTime3 = aggregate(floor(as.numeric(d[,6]) / 10000) / 100, by = list(d$V1, d$V2, d$V3), sum)
  d = filteredRawData[which(filteredRawData$V8 >= maxDef*0.90),]
  sumsTime10 = aggregate(floor(as.numeric(d[,6]) / 10000) / 100, by = list(d$V1, d$V2, d$V3), sum)
#  d = filteredRawData[which(filteredRawData$V8 >= maxDef*0.77),]
#  sumsTime33 = aggregate(floor(as.numeric(d[,6]) / 10000) / 100, by = list(d$V1, d$V2, d$V3), sum)
  d = filteredRawData
  sumsTime100 = aggregate(floor(as.numeric(d[,6]) / 10000) / 100, by = list(d$V1, d$V2, d$V3), sum)
  
  meanSumTime3   = aggregate(sumsTime3$x,   by = list(sumsTime3$Group.1,   sumsTime3$Group.3), mean)
  meanSumTime10  = aggregate(sumsTime10$x,  by = list(sumsTime10$Group.1,  sumsTime10$Group.3), mean)
#  meanSumTime33  = aggregate(sumsTime33$x,  by = list(sumsTime33$Group.1,  sumsTime33$Group.3), mean)
  meanSumTime100 = aggregate(sumsTime100$x, by = list(sumsTime100$Group.1, sumsTime100$Group.3), mean)
  
  line = modelName
  line = paste(line, as.numeric(stat$V2[1]), sep = " & ")
  line = paste(line, as.numeric(stat$V3[1]), sep = " & ")
  line = paste(line, as.numeric(stat$V10[1]), sep = " & ")
  line = paste(line, floor(as.numeric(stat$initNoFG[1]) / 1000000), sep = " & ")
  line = paste(line, floor(as.numeric(stat$initNonFG[1]) / 1000000), sep = " & ")
  line = paste(line, floor(as.numeric(stat$intiCFG[1]) / 1000000), sep = " & ")
  line = paste(line, floor(  meanSumTime3[3,3]), sep = " & ")
  line = paste(line, floor(  meanSumTime3[2,3]), sep = " & ")
  line = paste(line, floor(  meanSumTime3[1,3]), sep = " & ")
  line = paste(line, floor( meanSumTime10[3,3]), sep = " & ")
  line = paste(line, floor( meanSumTime10[2,3]), sep = " & ")
  line = paste(line, floor( meanSumTime10[1,3]), sep = " & ")
#  line = paste(line, floor( meanSumTime33[3,3]), sep = " & ")
#  line = paste(line, floor( meanSumTime33[2,3]), sep = " & ")
#  line = paste(line, floor( meanSumTime33[1,3]), sep = " & ")
  line = paste(line, floor(meanSumTime100[3,3]), sep = " & ")
  line = paste(line, floor(meanSumTime100[2,3]), sep = " & ")
  line = paste(line, floor(meanSumTime100[1,3]), sep = " & ")
  text = paste(text, line)
  text = paste(text, "\\\\\n")
  
  mean01 = c(mean01, as.numeric(stat$V2[1]))
  mean02 = c(mean02, as.numeric(stat$V3[1]))
  mean02_ = c(mean02_, as.numeric(stat$V10[1]))
  mean03 = c(mean03, floor(as.numeric(stat$initNoFG[1]) / 1000000))
  mean04 = c(mean04, floor(as.numeric(stat$initNonFG[1]) / 1000000))
  mean05 = c(mean05, floor(as.numeric(stat$intiCFG[1]) / 1000000))
  mean06 = c(mean06, floor(  meanSumTime3[3,3]))
  mean07 = c(mean07, floor(  meanSumTime3[2,3]))
  mean08 = c(mean08, floor(  meanSumTime3[1,3]))
  mean09 = c(mean09, floor( meanSumTime10[3,3]))
  mean10 = c(mean10, floor( meanSumTime10[2,3]))
  mean11 = c(mean11, floor( meanSumTime10[1,3]))
  mean12 = c(mean12, floor(meanSumTime100[3,3]))
  mean13 = c(mean13, floor(meanSumTime100[2,3]))
  mean14 = c(mean14, floor(meanSumTime100[1,3]))
}

line = " "
line = paste(line, mean(mean01), sep = " & ")
line = paste(line, mean(mean02), sep = " & ")
line = paste(line, mean(mean02_), sep = " & ")
line = paste(line, mean(mean03), sep = " & ")
line = paste(line, mean(mean04), sep = " & ")
line = paste(line, mean(mean05), sep = " & ")
line = paste(line, mean(mean06), sep = " & ")
line = paste(line, mean(mean07), sep = " & ")
line = paste(line, mean(mean08), sep = " & ")
line = paste(line, mean(mean09), sep = " & ")
line = paste(line, mean(mean10), sep = " & ")
line = paste(line, mean(mean11), sep = " & ")
line = paste(line, mean(mean12), sep = " & ")
line = paste(line, mean(mean13), sep = " & ")
line = paste(line, mean(mean14), sep = " & ")
line = paste(line, "\\\\\n")
cat(text)
cat(line)

