library(ggplot2)

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
p.i <- p.i + geom_segment(aes(x=0, xend=1, y=baseline.i[adj], yend=cosadd.i[adj]), lineend="round", size=.1)
p.i <- p.i + geom_segment(aes(x=0, xend=1, y=baseline.i[noun], yend=cosadd.i[noun]), lineend="round", size=.1)
#p.i <- p.i + ggtitle("Inflectional morphology")

p.i2 = ggplot()
p.i2 <- p.i2 + geom_segment(aes(x=0, xend=1, y=baseline.i[verb], yend=cosadd.i[verb]), lineend="round", size=.2, color='orange')
p.i2 <- p.i2 + geom_segment(aes(x=0, xend=1, y=baseline.i[adj], yend=cosadd.i[adj]), lineend="round", size=.2)
p.i2 <- p.i2 + geom_segment(aes(x=0, xend=1, y=baseline.i[noun], yend=cosadd.i[noun]), lineend="round", size=.2)
p.i2 <- p.i2 + annotate("text", x=.75, y=.16, label="verbs", size=3, color='orange')
#p.i2 <- p.i2 + ggtitle("Inflectional morphology")

p.i3 = ggplot()
p.i3 <- p.i3 + geom_segment(aes(x=0, xend=1, y=baseline.i[verb], yend=cosadd.i[verb]), lineend="round", size=.2, color='orange')
p.i3 <- p.i3 + geom_segment(aes(x=0, xend=1, y=baseline.i[adj], yend=cosadd.i[adj]), lineend="round", size=.2, color='blue')
p.i3 <- p.i3 + geom_segment(aes(x=0, xend=1, y=baseline.i[noun], yend=cosadd.i[noun]), lineend="round", size=.2)
p.i3 <- p.i3 + annotate("text", x=.75, y=.16, label="verbs", size=3, color='orange')
p.i3 <- p.i3 + annotate("text", x=.75, y=.12, label="adjectives", size=3, color='blue')
p.i3 <- p.i3 + annotate("text", x=.75, y=.08, label="nouns", size=3)
#p.i3 <- p.i3 + ggtitle("Inflectional morphology")

p.d = ggplot()
p.d <- p.d + geom_segment(aes(x=0, xend=1, y=baseline.d[nolexchange], yend=cosadd.d[nolexchange]), lineend="round", size=.1)
p.d <- p.d + geom_segment(aes(x=0, xend=1, y=baseline.d[lexchange], yend=cosadd.d[lexchange]), lineend="round", size=.1)
#p.d <- p.d + ggtitle("Derivational morphology")

p.d2 = ggplot()
p.d2 <- p.d2 + geom_segment(aes(x=0, xend=1, y=baseline.d[nolexchange], yend=cosadd.d[nolexchange]), lineend="round", size=.2, color='green3')
p.d2 <- p.d2 + geom_segment(aes(x=0, xend=1, y=baseline.d[lexchange], yend=cosadd.d[lexchange]), lineend="round", size=.2)
p.d2 <- p.d2 + annotate("text", x=.25, y=.72, label="purely syntactic", size=3, color='green3')
p.d2 <- p.d2 + annotate("text", x=.25, y=.76, label="semantic effects", size=3)
#p.d2 <- p.d2 + ggtitle("Derivational morphology")

p.n = ggplot()
p.n <- p.n + geom_segment(aes(x=0, xend=1, y=baseline.n[countrycap], yend=cosadd.n[countrycap]), lineend="round", size=.1)
p.n <- p.n + geom_segment(aes(x=0, xend=1, y=baseline.n[noncountrycap], yend=cosadd.n[noncountrycap]), lineend="round", size=.1)
#p.n <- p.n + ggtitle("Named entities")

p.n2 = ggplot()
p.n2 <- p.n2 + geom_segment(aes(x=0, xend=1, y=baseline.n[countrycap], yend=cosadd.n[countrycap]), lineend="round", size=.2, color='purple')
p.n2 <- p.n2 + geom_segment(aes(x=0, xend=1, y=baseline.n[noncountrycap], yend=cosadd.n[noncountrycap]), lineend="round", size=.2)
p.n2 <- p.n2 + annotate("text", x=.2, y=.86, label="country capitals", size=3, color='purple')
p.n2 <- p.n2 + annotate("text", x=.2, y=.82, label="other", size=3)
#p.n2 <- p.n2 + ggtitle("Named entities")

p.l = ggplot()
p.l <- p.l + geom_segment(aes(x=0, xend=1, y=baseline.l[gender], yend=cosadd.l[gender]), lineend="round", size=.1)
p.l <- p.l + geom_segment(aes(x=0, xend=1, y=baseline.l[nongender], yend=cosadd.l[nongender]), lineend="round", size=.1)
#p.l <- p.l + ggtitle("Lexical semantics")

p.l2 = ggplot()
p.l2 <- p.l2 + geom_segment(aes(x=0, xend=1, y=baseline.l[gender], yend=cosadd.l[gender]), lineend="round", size=.2, color='red')
p.l2 <- p.l2 + geom_segment(aes(x=0, xend=1, y=baseline.l[nongender], yend=cosadd.l[nongender]), lineend="round", size=.2)
p.l2 <- p.l2 + annotate("text", x=.15, y=.92, label="gender", size=3, color='red')
p.l2 <- p.l2 + annotate("text", x=.15, y=.88, label="other", size=3)
#p.l2 <- p.l2 + ggtitle("Lexical semantics")

offset=.1

fixplot = function(p) {
p <- p + xlab("") + ylab("Mean reciprocal rank")
p <- p + annotate("text", x=offset, y=-.05, label="Baseline", size=3)
p <- p + annotate("text", x=1-offset, y=-.05, label="Arithmetic", size=3)
p <- p + theme(panel.background=element_blank())
p <- p + theme(panel.grid.minor.y=element_blank(), panel.grid.major.y=element_line(colour="grey", size=.1))
p <- p + theme(panel.grid.minor.x=element_blank(), panel.grid.major.x=element_blank())
p <- p + theme(axis.ticks.x=element_blank())
p <- p + theme(axis.text.x=element_blank())
p <- p + scale_y_continuous(breaks=c(0,.1,.2,.3,.4,.5,.6,.7,.8,.9,1), limits=c(-.05, 1))
return(p)
}

p.i = fixplot(p.i)
p.i2 = fixplot(p.i2)
p.i3 = fixplot(p.i3)
p.d = fixplot(p.d)
p.d2 = fixplot(p.d2)
p.n = fixplot(p.n)
p.n2 = fixplot(p.n2)
p.l = fixplot(p.l)
p.l2 = fixplot(p.l2)

pdf('inflectional.pdf', width=4, height=4)
plot(p.i)
dev.off()

#pdf('inflectional2.pdf', width=4, height=4)
#plot(p.i2)
#dev.off()

pdf('inflectional3.pdf', width=4, height=4)
plot(p.i3)
dev.off()

pdf('derivational.pdf', width=4, height=4)
plot(p.d)
dev.off()

pdf('derivational2.pdf', width=4, height=4)
plot(p.d2)
dev.off()

pdf('lexical.pdf', width=4, height=4)
plot(p.l)
dev.off()

pdf('lexical2.pdf', width=4, height=4)
plot(p.l2)
dev.off()

pdf('named.pdf', width=4, height=4)
plot(p.n)
dev.off()

pdf('named2.pdf', width=4, height=4)
plot(p.n2)
dev.off()
