
acc.sort <- acc.agg[order(-acc.agg[,2]),]
rrimpr.sort <- rrimpr.agg[order(-rrimpr.agg[,2]),]
#rankimpr.sort <- rankimpr.agg[order(-rankimpr.agg[,2]),]
baserr.sort <- baserr.agg[order(-baserr.agg[,2]),]


#s <- data.frame(acc.sort, rrimpr.sort, rankimpr.sort)
s <- data.frame(acc.sort, baserr.sort, rrimpr.sort)
colnames(s) <- c("category", "accuracy", "category2", "BRR", "category3", "RRG")

write.csv(s, 'table.csv')
