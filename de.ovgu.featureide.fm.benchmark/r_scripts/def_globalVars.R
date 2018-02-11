file.sep <- .Platform$file.sep

dir.result <- "../diagrams2"
dir.data <- "../results"
dir.data.readall <- FALSE

algos_def <- c(
  "FG_Improved ",
  "CFG_Improved",
  "SAT_Improved",
  "FG_Naive    ",
  "CFG_Naive   ",
  "Splar       "
)

myColors_def <- c(
  "darkgreen",
  "blue",
  "red",
  "yellow",
  "lightblue",
  "green"
)

diagram.fontSize <- 2.4
diagram.regSpan <- 0.2
diagram.alpha <- 0.6

myColorScheme3 <- c(
  rgb(1.000, 0.827, 0.000, 1),
  rgb(0.875, 0.000, 0.494, 1),
  rgb(0.000, 0.741, 0.741, 1)
)
myColorScheme4 <- c(
  rgb(1.000, 0.910, 0.000, 1),
  rgb(0.000, 0.878, 0.502, 1),
  rgb(0.392, 0.270, 0.875, 1),
  rgb(1.000, 0.286, 0.000, 1)
)

ls = length(selectionIndex)
if (ls == 2) {
  diagram.color <- myColorScheme4[c(2,4)]
} else if (ls == 3) {
  #diagram.color <- myColorScheme3
  diagram.color <- myColorScheme4[c(2,3,4)]
} else if (ls == 4) {
  diagram.color <- myColorScheme4
} else {
  diagram.color <- myColors_def[selectionIndex]
}
data.algorithms <- algos_def[selectionIndex]

remove(ls)
remove(myColors_def)
remove(algos_def)
remove(myColorScheme3)
remove(myColorScheme4)
remove(selectionIndex)
