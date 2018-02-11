dir.data.sub <- list.dirs(path = dir.data, recursive = FALSE)	

# Read all data dirs
if (dir.data.readall) {
  print(dir.data.sub)
  dir.models <- rep(list(vector()), length(dir.data.sub))
  for (i in seq(along=dir.data.sub)) {
    dir.models[[i]] <- append(dir.models[[i]], list.dirs(path = dir.data.sub[i], recursive = FALSE))
  }
  table.raw.initTimes <- readCSVs(paste(dir.data.sub, "initTimes.csv", sep = file.sep))
  table.raw.fmStatistics <- readCSVs(paste(dir.data.sub, "fmStatistic.csv", sep = file.sep))

# Read only last data dir
} else {
  dir.data.sub <- min(dir.data.sub)
  print(dir.data.sub )
  dir.models <- list(list.dirs(path = dir.data.sub , recursive = FALSE))
  table.raw.initTimes = readCSVs(paste(dir.data.sub, "initTimes.csv", sep = file.sep))
  table.raw.fmStatistics <- readCSVs(paste(dir.data.sub, "fmStatistic.csv", sep = file.sep))
}

model.paths <- unlist(dir.models)
model.paths.segments <- unlist(strsplit(model.paths, file.sep))
model.names <- model.paths.segments[seq(4,length(model.paths.segments),4)]

table.raw.times <- readCSVs(paste(model.paths, "times.csv", sep = file.sep))
remove(model.paths)
remove(model.paths.segments)
remove(dir.models)

