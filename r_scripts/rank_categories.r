ranks = data.frame(cos.agg$category)
colnames(ranks) <- "original"

ranks$acc = with(acc.agg, category[order(-data)])
ranks$rrimpr = with(rrimpr.agg, category[order(-data)])
ranks$rankimpr = with(rankimpr.agg, category[order(-data)])
ranks$diffsim = with(diffsim.agg, category[order(-data)])
ranks$w3w4 = with(w3w4.agg, category[order(-data)])


