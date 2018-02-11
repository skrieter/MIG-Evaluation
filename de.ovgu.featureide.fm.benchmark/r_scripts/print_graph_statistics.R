text = ""

list01 = vector()
list02 = vector()
list03 = vector()
list04 = vector()

sortedModelNames = as.vector(raw_fmStatistics[order(raw_fmStatistics$V2),]$V1)
sortedModelNames = sortedModelNames[-(1:12)]
sortedModelNames = sortedModelNames[-which(sortedModelNames=="linux")]

for (i in seq(along=sortedModelNames)) {
  modelName = sortedModelNames[i]
  print(modelName)
  
  # Data
  stat = orgFmTable[which(orgFmTable$V1==modelName),]
  
  line = modelName
  line = paste(line, as.numeric(stat$V2[1]), sep = " & ")
  line = paste(line, as.numeric(stat$V3[1]), sep = " & ")
  line = paste(line, as.numeric(stat$V10[1]), sep = " & ")
  line = paste(line, as.numeric(stat$V16[1]), sep = " & ")
  text = paste(text, line)
  text = paste(text, "\\\\\n")
  
  list01 = c(list01, as.numeric(stat$V2[1]))
  list02 = c(list02, as.numeric(stat$V3[1]))
  list03 = c(list03, as.numeric(stat$V10[1]))
  list04 = c(list04, as.numeric(stat$V16[1]))
}

cat(text)

line = " "
line = paste(line, mean(list01), sep = " & ")
line = paste(line, mean(list02), sep = " & ")
line = paste(line, mean(list03), sep = " & ")
line = paste(line, mean(list04), sep = " & ")
line = paste(line, "\\\\\n")
cat(line)

line = " "
line = paste(line, min(list01), sep = " & ")
line = paste(line, min(list02), sep = " & ")
line = paste(line, min(list03), sep = " & ")
line = paste(line, min(list04), sep = " & ")
line = paste(line, "\\\\\n")
cat(line)

line = " "
line = paste(line, max(list01), sep = " & ")
line = paste(line, max(list02), sep = " & ")
line = paste(line, max(list03), sep = " & ")
line = paste(line, max(list04), sep = " & ")
line = paste(line, "\\\\\n")
cat(line)

