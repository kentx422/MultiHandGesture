##exceptDevice
#Galaxy-S5
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_Galaxy-S5-atonomura_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#Galaxy-S6edge
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_Galaxy-S6edge-dyamashita_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#Xperia-Z3
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_Xperia-Z3-smorimura_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#Xperia-Z5
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_Xperia-Z5-tyamamoto_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#nexus7-2012
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_nexus7-2012-hmurakami_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#nexus7-2013
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptDevice\\exceptDevice_nexus7-2013-haida_30subjects.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)
