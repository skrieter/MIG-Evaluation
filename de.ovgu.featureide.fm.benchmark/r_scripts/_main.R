selectionIndex <- c(
  1 # FG_Improved
  ,2 # CFG_Improved
  ,3 # SAT_Improved
)

source("def_globalVars.R")
source("def_functions.R")

### Read Data ###
source("read_data.R")
source("calc_fmTable.R")

fmTable <- table.fm.org[table.fm.org$Group.3 == "R",]
fmTable <- fmTable[order(fmTable$V2),]
model.names.sorted = unique(fmTable$V1)
fmTable <-  fmTable[!(fmTable$V1 %in% model.names.sorted[1:2]),]
source("plot_init_combined4.R")

source("print_models.R")
source("print_average_values.R")
source("print_pValues.R")

source("plot_model.R")

