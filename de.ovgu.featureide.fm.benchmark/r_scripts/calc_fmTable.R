initTimes <- aggregate(as.numeric(table.raw.initTimes$V4), by = list(table.raw.initTimes$V1,table.raw.initTimes$V2), mean)
names(initTimes)[names(initTimes)=="Group.1"] <- "V1"
initTimes <- initTimes[initTimes$V1 %in% model.names,]
initCFG   <- initTimes[initTimes[2] == "FGStrongComplete",-2]
names(initCFG)[names(initCFG)=="x"] <- "intiCFG"
initNonFG <- initTimes[initTimes[2] == "FGNonComplete",-2]
names(initNonFG)[names(initNonFG)=="x"] <- "initNonFG"
initNoFG  <- initTimes[initTimes[2] == "NoFG",-2]
names(initNoFG)[names(initNoFG)=="x"] <- "initNoFG"

meanSumTime <- data.frame()
meanSumSat  <- data.frame()
meanMaxSelections  <- data.frame()
minMaxSelections  <- data.frame()
maxMaxSelections  <- data.frame()
for (modelName in model.names) {
  print(modelName)
  
  # Data
  filteredRawData = table.raw.times[table.raw.times[,3] %in% data.algorithms & table.raw.times[,1] == modelName,]
  type = substr(filteredRawData$V2, 1, 1)
  filteredRawData = cbind(filteredRawData, type)
  #count = length(unique(filteredRawData[which(type=="R"),2]))
  
  countSelections = as.numeric(filteredRawData[,4])
  rawTime = floor(as.numeric(filteredRawData[,6]) / 10000) / 100
  rawSat  = as.numeric(filteredRawData[,7])
  
  filterList = list(filteredRawData$V1, filteredRawData$V2, filteredRawData$V3, filteredRawData$type)
  maxSelections = aggregate(countSelections, by = filterList, max)
  sumsTime = aggregate(rawTime, by = filterList, sum)
  sumsSat  = aggregate(rawSat,  by = filterList, sum)

  groupList = list(sumsTime$Group.1, sumsTime$Group.3, sumsTime$Group.4)
  
  meanSumTime = rbind(meanSumTime, aggregate(sumsTime$x, by = groupList, mean))
  meanSumSat  = rbind(meanSumSat,  aggregate(sumsSat$x,  by = groupList, mean))
  meanMaxSelections = rbind(meanMaxSelections, aggregate(maxSelections$x, by = groupList, mean))
  minMaxSelections  = rbind( minMaxSelections, aggregate(maxSelections$x, by = groupList,  min))
  maxMaxSelections  = rbind( maxMaxSelections, aggregate(maxSelections$x, by = groupList,  max))
}
remove(filteredRawData)
remove(countSelections)
remove(groupList)
remove(filterList)
remove(rawTime)
remove(rawSat)
remove(maxSelections)
remove(sumsTime)
remove(sumsSat)
remove(modelName)

names(meanSumTime)[names(meanSumTime)=="Group.1"] <- "V1"
names(meanSumSat) [names(meanSumSat) =="Group.1"] <- "V1"
names(meanMaxSelections)[names(meanMaxSelections)=="Group.1"] <- "V1"
names(minMaxSelections) [names(minMaxSelections) =="Group.1"] <- "V1"
names(maxMaxSelections) [names(maxMaxSelections) =="Group.1"] <- "V1"
names(meanSumTime)[names(meanSumTime)=="x"] <- "meanSumTime"
names(meanSumSat) [names(meanSumSat) =="x"] <- "meanSumSat"
names(meanMaxSelections)[names(meanMaxSelections)=="x"] <- "meanMaxSelections"
names(minMaxSelections) [names(minMaxSelections) =="x"] <- "minMaxSelections"
names(maxMaxSelections) [names(maxMaxSelections) =="x"] <- "maxMaxSelections"

table.fm.org <- table.raw.fmStatistics
table.fm.org <- merge(initCFG, table.fm.org, all = F)
table.fm.org <- merge(initNonFG, table.fm.org, all = F)
table.fm.org <- merge(initNoFG, table.fm.org, all = F)
table.fm.org <- merge(meanSumTime, table.fm.org, all = F)
table.fm.org <- merge(meanSumSat, table.fm.org, all = F)
table.fm.org <- merge(meanMaxSelections, table.fm.org, all = F)
table.fm.org <- merge(minMaxSelections, table.fm.org, all = F)
table.fm.org <- merge(maxMaxSelections, table.fm.org, all = F)

remove(initCFG)
remove(initNoFG)
remove(initNonFG)
remove(initTimes)
remove(maxMaxSelections)
remove(meanMaxSelections)
remove(meanSumSat)
remove(meanSumTime)
remove(minMaxSelections)

