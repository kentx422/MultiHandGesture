##testOnlyUntilDecember
dir("K:\\github\\MultiHandGesture\\ServerPC\\ServerOfMultiGestureForSubjectiveExperimentUntillDecember\\analysis\\result\\ÅyimportantÅz20161126001905\\extractFromTempCombain","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\ServerOfMultiGestureForSubjectiveExperimentUntillDecember\\analysis\\result\\ÅyimportantÅz20161126001905\\extractFromTempCombain\\tempCombain_nexus7-2013-amiyoshi_nexus7-2013-haida2.csv",header=T)
data <- subset(nd, gesture=="toRight" | gesture=="upRight" | gesture=="downRight" | gesture=="toLeft" | gesture=="upLeft" | gesture=="downLeft" | gesture=="toTop" | gesture=="upTop" | gesture=="downTop" | gesture=="toBottom" | gesture=="upBottom" | gesture=="downBottom" , select=c(1:5))
library(rpart)
nt <-rpart(gesture~., data=nd,control = rpart.control(minsplit=1))
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
#text(nt, use.n=TRUE)
text(nt, use.n=FALSE)
printcp(nt)

#AutoHotKey: {RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT}{RIGHT},