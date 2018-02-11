# Online Time
sat = fmTable$meanSumTime[which(fmTable$Group.2 == data.algorithms[3])]
nmig = fmTable$meanSumTime[which(fmTable$Group.2 == data.algorithms[1])]
cmig = fmTable$meanSumTime[which(fmTable$Group.2 == data.algorithms[2])]

print(sat)
mean(sat)
mean(nmig)
mean(cmig)

wilcox.test(nmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  cmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(nmig, cmig, paired=TRUE, alternative="greater", conf.level = 0.99)

# Offline Time
sat = fmTable$initNoFG[which(fmTable$Group.2 == data.algorithms[3])]
nmig = fmTable$initNonFG[which(fmTable$Group.2 == data.algorithms[1])]
cmig = fmTable$intiCFG[which(fmTable$Group.2 == data.algorithms[2])]

print(sat)
mean(sat)
mean(nmig)
mean(cmig)

wilcox.test(nmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  cmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(nmig, cmig, paired=TRUE, alternative="greater", conf.level = 0.99)




# Offline + Online Time
sat  = (3*fmTable$meanSumTime[which(fmTable$Group.2 == algos[3])]) + (fmTable$initNoFG[which(fmTable$Group.2 == algos[3])] / 1000000)
nmig = (3*fmTable$meanSumTime[which(fmTable$Group.2 == algos[1])]) + (fmTable$initNonFG[which(fmTable$Group.2 == algos[1])] / 1000000)
cmig = (3*fmTable$meanSumTime[which(fmTable$Group.2 == algos[2])]) + (fmTable$intiCFG[which(fmTable$Group.2 == algos[2])] / 1000000)

print(sat)
mean(sat)
mean(nmig)
mean(cmig)

wilcox.test(nmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  cmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(nmig, cmig, paired=TRUE, alternative="greater", conf.level = 0.99)


# Online Time For Each Single Measurment
names = as.vector(unique(fmTable$V1))
sat = vector()
nmig = vector()
cmig = vector()

prefilteredRawData = subset(table.raw.times, V3 %in% data.algorithms & startsWith(as.character(V2), "R"))

for(i in seq(along = names)) {
  name = names[i]
  cat(length(names) - i)
  cat(" - ")
  cat(name)
  cat("\n")
  
  filteredRawData = subset(prefilteredRawData, V1 == name)
  
 # filteredRawData = rawData[which(rawData[,3] %in% data.algorithms),]
  #filteredRawData = filteredRawData[which(startsWith(as.vector(filteredRawData[,2]), "R")),]
  values = floor(as.numeric(filteredRawData[,6]) / 1000000)
  
  sat  = append(sat,  values[which(filteredRawData$V3==data.algorithms[3])])
  nmig = append(nmig, values[which(filteredRawData$V3==data.algorithms[1])])
  cmig = append(cmig, values[which(filteredRawData$V3==data.algorithms[2])])
}

mean(sat)
mean(nmig)
mean(cmig)

wilcox.test(nmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, sat,  paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(cmig, nmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(sat,  cmig, paired=TRUE, alternative="greater", conf.level = 0.99)
wilcox.test(nmig, cmig, paired=TRUE, alternative="greater", conf.level = 0.99)

cat("Finished!")
