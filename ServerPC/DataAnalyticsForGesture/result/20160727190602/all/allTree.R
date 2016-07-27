#all
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160726215533\\all","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160726215533\\all\\all_6subjects_6devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE, all=TRUE )
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)