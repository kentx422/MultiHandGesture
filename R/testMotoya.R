dir("K:\\github\\MultiHandGesture\\R","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\R\\all20160715195743.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
