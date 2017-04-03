if (exists('d')) {
    rm('d')
}
for (filename in filenames) {
    if (exists('d')) {
        d = rbind(d, read.csv(filename))
    } else {
        d = read.csv(filename)
    }
    # define baseline as the best of either w2 or w3
    d$w3rank = d$baserank
    d$baserank[d$domainsimrank < d$w3rank] = d$domainsimrank[d$domainsimrank < d$w3rank]

    # add a rank improvement ternary variable
    d$rankimpr = 0
    d$rankimpr[d$addrank > d$baserank] = -1
    d$rankimpr[d$addrank < d$baserank] = 1
}
