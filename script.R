#wv_wv = read.csv("results/w2v_w2v.txt")
#wv_gl = read.csv("results/w2v_glove.txt")
#
#my_wv = read.csv("results/myset_w2v.txt")
#my_gl = read.csv("results/myset_glove.txt")
#
#ms_wv = read.csv("results/ms_w2v.txt")
#ms_gl = read.csv("results/ms_glove.txt")

#levels(wv_wv$category) = c("CapitalOf (common)", "CapitalOf (all)", "CityInState", "HasCurrency", "Man-Woman", "AdjToAdv", "Opposite", "Comparative", "Superlative", "PresParticiple", "NationalityAdj", "PastTense", "Plural", "3rdSingular")
#levels(wv_gl$category) = c("CapitalOf (common)", "CapitalOf (all)", "CityInState", "HasCurrency", "Man-Woman", "AdjToAdv", "Opposite", "Comparative", "Superlative", "PresParticiple", "NationalityAdj", "PastTense", "Plural", "3rdSingular")

#levels(my_wv$category) = c("Antonym", "InstanceOf", "MemberOf", "PartOf")
#levels(my_gl$category) = c("Antonym", "InstanceOf", "MemberOf", "PartOf")


#d = wv_wv
d = read.csv("stats_bats.csv")

print(summary(d))

with(d, aggregate(cos ~ category, FUN=mean))
with(d, aggregate(w3w4 ~ category, FUN=mean))
with(d, aggregate(cos - w3w4 ~ category, FUN=mean))
with(d, aggregate(cos > w3w4 ~ category, FUN=mean))
#with(d, aggregate(w2w4 ~ category, FUN=mean))

d$rrdiff = 1/d$addrank - 1/d$baslinerank
d$rankshift = 0
d$rankshift[d$addrank < d$baslinerank] = -1
d$rankshift[d$addrank > d$baslinerank] = 1 

d$cosdiff = d$cos - d$w3w4

print("percentage good rank shift")
for(cat in levels(d$category)) {
    print(cat)
    dsub = d[d$category==cat,]
    print(with(dsub, sum(rankshift==-1) / sum(rankshift!=0)))
}

with(d, aggregate(1/baslinerank ~ category, FUN=mean))
with(d, aggregate(1/addrank ~ category, FUN=mean))
with(d, aggregate(1/addrank - 1/baslinerank ~ category, FUN=mean))



for(cat in levels(d$category)) {
    print(cat)
    dsub = d[d$category==cat,]
    print("basline accuracy")
    print(sum(dsub$baslinerank == 1) / nrow(dsub))
    print("3CosAdd accuracy")
    print(sum(dsub$addrank == 1) / nrow(dsub))
    print(sum(dsub$addrank > dsub$baslinerank))
    print(sum(dsub$addrank < dsub$baslinerank))
    print("LORI")
    print(log(sum(dsub$addrank < dsub$baslinerank) / sum(dsub$addrank > dsub$baslinerank)) / log(2))
    print("mean reciprocal rank difference from basline")
    print(mean(1/dsub$addrank - 1/dsub$baslinerank))
}




# plots

smoothing_bandwidth = 'nrd0'
adjust = 1
kernel = 'g'

par(mfrow = c(7,6))
for(cat in levels(d$category)) {
    dsub = d[d$category==cat,]
    with(dsub, plot(density(cos, smoothing_bandwidth, adjust, kernel),
                main = cat))
    lines(density(dsub$w3w4, smoothing_bandwidth, adjust, kernel), lty=3)
}

par(mfrow = c(7,6))
for(cat in levels(d$category)) {
    dsub = d[d$category==cat,]
    with(dsub, plot(density(1/addrank, smoothing_bandwidth, adjust, kernel),
                main = cat))
    lines(density(1/dsub$baslinerank, smoothing_bandwidth, adjust, kernel), lty=3)
}

par(mfrow = c(7,6))
d$rankshift = 0
d$rankshift[d$addrank < d$baslinerank] = -1
d$rankshift[d$addrank > d$baslinerank] = 1 
for(cat in levels(d$category)) {
    dsub = d[d$category==cat,]
    rs = as.factor(dsub$rankshift)
    levels(rs) = c('better', 'same', 'worse')
    plot(rs, main=cat)
}
