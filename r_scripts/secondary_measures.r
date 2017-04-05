# Generate secondary measures (aggregates over categories) for analogy experiment data

# depends on d being the variable holding main data
agg = function(data1) {
    x = aggregate(data1 ~ d$category, FUN=mean)
    colnames(x) <- c("category", "data")
    return(x)
}

# compute all possible correlations over a list of vectors
boxcor = function(datas) {
    for (i in 1:(length(datas)-1)) {
        for (j in (i+1):length(datas)) {
            print(i)
            print(j)
            print(cor.test(unlist(datas[i]), unlist(datas[j])))
        }
    }
}

d$cosimpr[d$w3w4 > d$w2w4] = d$cos[d$w3w4>d$w2w4] - d$w3w4[d$w3w4>d$w2w4]
d$cosimpr[d$w3w4 <= d$w2w4] = d$cos[d$w3w4<=d$w2w4] - d$w2w4[d$w3w4<=d$w2w4]

cos.agg = agg(d$cos)
diffsim.agg = agg(d$diffsim)
acc.agg = agg(d$addrank == 1)
rrimpr.agg = agg(1/d$addrank - 1/d$baserank)
addrr.agg = agg(1/d$addrank)
baserr.agg = agg(1/d$baserank)
domainrr.agg = agg(1/d$domainsimrank)
diffrr.agg = agg(1/d$diffrank)
w3w4.agg = agg(d$w3w4)
w2w4.agg = agg(d$w2w4)
rrimprdiff.agg = agg(1/d$diffrank - 1/d$baserank)

cosimpr.agg = agg(d$cosimpr)

addrank.agg = agg(d$addrank)
baserank.agg = agg(d$baserank)
domainrank.agg = agg(d$domainsimrank)
diffrank.agg = agg(d$diffrank)
rankimpr.agg = agg(d$rankimpr)

baseacc.agg = agg(d$baserank==1)
w3acc.agg = agg(d$w3rank==1)
w2acc.agg = agg(d$domainsimrank==1)