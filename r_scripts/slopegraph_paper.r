library(ggplot2)

source('multiplot.r')

lex = c(1:20, 23:25, 27:35, 47:51, 57:66, 86:94, 101:115, 118:141)
infl = c(69:71, 73:85, 95:100, 116:117, 142:147)
deriv = c(37:46, 67, 68)
named = c(21, 22, 26, 36, 52:56, 72)

# within categories:

# inflectional
adj = c(1, 2, 9, 10, 17:22)
noun = c(5, 7, 8, 23, 24)
verb = c(3, 4, 6, 11:16, 25:30)

# derivational
lexchange = c(1, 2, 4, 6, 7, 8, 12)
nolexchange = c(3, 5, 9, 10, 11)

# lexical
gender = c(42, 46)
nongender = c(1:41, 43:45, 47:95)

# named entity
countrycap = c(1,2,5)
noncountrycap = c(3,4,6,7,8,9,10)


plotdata = data.frame(baserr.agg[,2], addrr.agg[,2], addrr.agg[,1])

colnames(plotdata) <- c("baseline", "cosadd", "category")

baseline.i = baserr.agg[infl, 2]
cosadd.i = addrr.agg[infl, 2]
baseline.d = baserr.agg[deriv, 2]
cosadd.d = addrr.agg[deriv, 2]
baseline.n = baserr.agg[named, 2]
cosadd.n = addrr.agg[named, 2]
baseline.l = baserr.agg[lex, 2]
cosadd.l = addrr.agg[lex, 2]

p.i = ggplot()
p.i <- p.i + geom_segment(aes(x=0, xend=1, y=baseline.i[verb], yend=cosadd.i[verb]), lineend="round", size=.1)
p.i <- p.i + geom_segment(aes(x=0, xend=1, y=baseline.i[adj], yend=cosadd.i[adj]), lineend="round", size=.3, linetype='dotted')
p.i <- p.i + geom_segment(aes(x=0, xend=1, y=baseline.i[noun], yend=cosadd.i[noun]), lineend="round", size=.2, linetype='dashed')
p.i <- p.i + ggtitle("Inflectional morphology")

p.d = ggplot()
p.d <- p.d + geom_segment(aes(x=0, xend=1, y=baseline.d[nolexchange], yend=cosadd.d[nolexchange]), lineend="round", size=.3, linetype='dotted')
p.d <- p.d + geom_segment(aes(x=0, xend=1, y=baseline.d[lexchange], yend=cosadd.d[lexchange]), lineend="round", size=.1)
p.d <- p.d + ggtitle("Derivational morphology")

p.n = ggplot()
p.n <- p.n + geom_segment(aes(x=0, xend=1, y=baseline.n[countrycap], yend=cosadd.n[countrycap]), lineend="round", size=.3, linetype='dotted')
p.n <- p.n + geom_segment(aes(x=0, xend=1, y=baseline.n[noncountrycap], yend=cosadd.n[noncountrycap]), lineend="round", size=.1)
p.n <- p.n + ggtitle("Named entities")

p.l = ggplot()
p.l <- p.l + geom_segment(aes(x=0, xend=1, y=baseline.l[gender], yend=cosadd.l[gender]), lineend="round", size=.3, linetype='dotted')
p.l <- p.l + geom_segment(aes(x=0, xend=1, y=baseline.l[nongender], yend=cosadd.l[nongender]), lineend="round", size=.1)
p.l <- p.l + ggtitle("Lexical semantics")


offset=.1

p.i <- p.i + xlab("") + ylab("Mean reciprocal rank")
p.i <- p.i + annotate("text", x=offset, y=-.05, label="Baseline", size=3)
p.i <- p.i + annotate("text", x=1-offset, y=-.05, label="3CosAdd", size=3)
p.i <- p.i + theme(panel.background=element_blank())
p.i <- p.i + theme(panel.grid.minor.y=element_blank(), panel.grid.major.y=element_line(colour="grey", size=.1))
p.i <- p.i + theme(panel.grid.minor.x=element_blank(), panel.grid.major.x=element_blank())
p.i <- p.i + scale_y_continuous(breaks=c(0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1))
p.i <- p.i + theme(axis.ticks.x=element_blank())
p.i <- p.i + theme(axis.text.x=element_blank())

#p.d <- p.d + xlab("") + ylab("Reciprocal rank")
p.d <- p.d + xlab("") + ylab("")
p.d <- p.d + annotate("text", x=offset, y=-.05, label="Baseline", size=3)
p.d <- p.d + annotate("text", x=1-offset, y=-.05, label="3CosAdd", size=3)
p.d <- p.d + theme(panel.background=element_blank())
p.d <- p.d + theme(panel.grid.minor.y=element_blank(), panel.grid.major.y=element_line(colour="grey", size=.1))
p.d <- p.d + theme(panel.grid.minor.x=element_blank(), panel.grid.major.x=element_blank())
p.d <- p.d + scale_y_continuous(breaks=c(0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1))
p.d <- p.d + theme(axis.ticks.x=element_blank())
p.d <- p.d + theme(axis.text.x=element_blank())

#p.l <- p.l + xlab("") + ylab("Reciprocal rank")
p.l <- p.l + xlab("") + ylab("")
p.l <- p.l + annotate("text", x=offset, y=-.05, label="Baseline", size=3)
p.l <- p.l + annotate("text", x=1-offset, y=-.05, label="3CosAdd", size=3)
p.l <- p.l + theme(panel.background=element_blank())
p.l <- p.l + theme(panel.grid.minor.y=element_blank(), panel.grid.major.y=element_line(colour="grey", size=.1))
p.l <- p.l + theme(panel.grid.minor.x=element_blank(), panel.grid.major.x=element_blank())
p.l <- p.l + scale_y_continuous(breaks=c(0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1))
p.l <- p.l + theme(axis.ticks.x=element_blank())
p.l <- p.l + theme(axis.text.x=element_blank())

#p.n <- p.n + xlab("") + ylab("Reciprocal rank")
p.n <- p.n + xlab("") + ylab("")
p.n <- p.n + annotate("text", x=offset, y=-.05, label="Baseline", size=3)
p.n <- p.n + annotate("text", x=1-offset, y=-.05, label="3CosAdd", size=3)
p.n <- p.n + theme(panel.background=element_blank())
p.n <- p.n + theme(panel.grid.minor.y=element_blank(), panel.grid.major.y=element_line(colour="grey", size=.1))
p.n <- p.n + theme(panel.grid.minor.x=element_blank(), panel.grid.major.x=element_blank())
p.n <- p.n + scale_y_continuous(breaks=c(0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1))
p.n <- p.n + theme(axis.ticks.x=element_blank())
p.n <- p.n + theme(axis.text.x=element_blank())

pdf('slopegraph.pdf', width=10.5, height=3)
multiplot(p.i, p.d, p.n, p.l, cols=4)
dev.off()
