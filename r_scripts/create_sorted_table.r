
acc.sort <- acc.agg[order(-acc.agg[,2]),]
rrimpr.sort <- rrimpr.agg[order(-rrimpr.agg[,2]),]
rankimpr.sort <- rankimpr.agg[order(-rankimpr.agg[,2]),]


s <- data.frame(acc.sort, rrimpr.sort, rankimpr.sort)
colnames(s) <- c("category.1", "accuracy", "category.2", "MRR improvement", "category.3", "rank improved?")

write.csv(s, 'table.csv')