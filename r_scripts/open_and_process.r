if (exists('d')) {
    rm('d')
}
for (filename in filenames) {
    dtemp = read.csv(filename)
    # define baseline as the best of either w2 or w3
    dtemp$w3rank = dtemp$baserank
    dtemp$baserank[dtemp$domainsimrank < dtemp$w3rank] = dtemp$domainsimrank[dtemp$domainsimrank < dtemp$w3rank]

    # add a rank improvement ternary variable
    dtemp$rankimpr = 0
    dtemp$rankimpr[dtemp$addrank > dtemp$baserank] = -1
    dtemp$rankimpr[dtemp$addrank < dtemp$baserank] = 1

    if (exists('d')) {
        d = rbind(d, dtemp)
    } else {
        d = dtemp
    }
}
