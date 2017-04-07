library(ggplot2)

lex = c(1:20, 23:25, 27:35, 47:51, 57:66, 86:94, 101:115, 118:141)
infl = c(69:71, 73:85, 95:100, 116:117, 142:147)
deriv = c(37:46, 67, 68)
named = c(21, 22, 26, 36, 52:56, 72)

plotdata = data.frame(baserr.agg[,2], addrr.agg[,2], addrr.agg[,1])

colnames(plotdata) <- c("baseline", "cosadd", "category")

for (source in list(lex, infl, deriv, named)) {

p <- ggplot(plotdata[lex,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p <- ggplot(plotdata[infl,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p <- ggplot(plotdata[deriv,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p <- ggplot(plotdata[named,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))

p<-p + theme(panel.background = element_blank())
p<-p + theme(panel.grid=element_blank())
p<-p + theme(axis.ticks=element_blank())
p<-p + theme(axis.text=element_blank())
p<-p + theme(panel.border=element_blank())
p<-p + xlab("") + ylab("Amount Used")
p<-p + theme(axis.title.y=theme_text(vjust=3))
#p<-p + xlim((0-12),(months+12))
p<-p + ylim(0,(1.2*(max(a$year3,a$year1))))
}
