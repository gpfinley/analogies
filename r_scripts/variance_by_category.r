for (level in levels(d$category)) {
    print(level)
    print(with(d[d$category==level,], sd(rankimpr)))
#    print(with(d[d$category==level,], sd(1/addrank-1/baserank)))
#    print(with(d[d$category==level,], sd(diffsim)))
}