sortedStatistics = table.raw.fmStatistics[order(table.raw.fmStatistics$V2),]
sortedStatistics = sortedStatistics[-c((1:12),(130:133)),]

cat(min(as.numeric(sortedStatistics$V3)))
cat(max(as.numeric(sortedStatistics$V3)))
cat(min(as.numeric(sortedStatistics$V2)))
cat(max(as.numeric(sortedStatistics$V2)))
