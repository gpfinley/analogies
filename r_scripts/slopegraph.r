library(ggplot2)

lex = c(1:20, 23:25, 27:35, 47:51, 57:66, 86:94, 101:115, 118:141)
infl = c(69:71, 73:85, 95:100, 116:117, 142:147)
deriv = c(37:46, 67, 68)
named = c(21, 22, 26, 36, 52:56, 72)

plotdata = data.frame(baserr.agg[,2], addrr.agg[,2], addrr.agg[,1])

colnames(plotdata) <- c("baseline", "cosadd", "category")

p.l <- ggplot(plotdata[lex,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p.i <- ggplot(plotdata[infl,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p.d <- ggplot(plotdata[deriv,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
p.n <- ggplot(plotdata[named,]) + geom_segment(aes(x=0, xend=1, y=baseline, yend=cosadd))
