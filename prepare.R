library(hash)

# todo: make this work? or abandon

getdata = function(resultsfile, vocabfile){
    d = read.csv(resultsfile)
    d$rrdiff = 1/d$addrank - 1/d$baserank
    d$rankshift = 0
    d$rankshift[d$addrank < d$baserank] = -1
    d$rankshift[d$addrank > d$baserank] = 1 
    d$cosdiff = d$cos - d$w3w4

    v = read.csv(vocabfile, sep=' ', header=F)
    h = hash(v$V1, v$V2)
    d$freq1 = 0
    d$freq2 = 0
    d$freq3 = 0
    d$freq4 = 0
    for (i in 1:nrow(d)) {
        thesplit = strsplit(as.character(d$analogy[i]), '::')
        w1 = strsplit(thesplit[1], ':')[1]
        w2 = strsplit(thesplit[1], ':')[2]
        w3 = strsplit(thesplit[2], ':')[1]
        w4 = strsplit(thesplit[2], ':')[2]
        d$freq1[i] = h[w1]
        d$freq2[i] = h[w2]
        d$freq3[i] = h[w3]
        d$freq4[i] = h[w4]
    }

    return (d)
}
