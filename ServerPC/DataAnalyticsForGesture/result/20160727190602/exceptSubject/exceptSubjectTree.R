##exceptSubjectTree
#dyamashita
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject\\exceptSubject_dyamashita_30devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#hmurakami
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject\\exceptSubject_hmurakami_30devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#kmatsui
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject\\exceptSubject_kmatsui_30devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#kohei
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject\\exceptSubject_kohei_30devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#ksoma
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\exceptSubject\\exceptSubject_SHU_30devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)

#SHU
dir("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\subject","csv$")
nd <- read.csv("K:\\github\\MultiHandGesture\\ServerPC\\DataAnalyticsForGesture\\result\\20160727190602\\subject\\subject_SHU_6devices.csv",header=T)
data <- subset(nd, class=="hide" | class=="slash" | class=="roll" | class=="up" | class=="down", select=c(1:4))
library(rpart)
nt <-rpart(class~., data=nd)
nt
par(xpd = NA)
plot(nt, uniform=TRUE)
text(nt, use.n=TRUE)
printcp(nt)
