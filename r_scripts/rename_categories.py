import re
import fileinput

# Convert categories from their original names in the dataset to abbreviated names suitable for a table

cats = """: Action:Action Attribute  X is a Y kind of action
X is Y kind of action
: Action:Action Nonattribute  X cannot be done in a Y manner
X cannot be done in Y manner
: Action:Object  someone perform the action X on Y
action X performed on Y
: Action:Object Attribute  someone will X an object that is Y
X an object that is Y
: Action:Object Nonattribute  the result of action X does not produce an object with Y
action X not produce s/t with Y
: Action:Recipient  to X is to have a Y receive some object/service/idea
to X is to have a Y receive some object/service/idea
: Action:Resultant Attribute (verb:noun/adjective)  the action X results Y or things that are Y
the action X results Y or things that are Y
: Action/Activity:Goal  someone/something will X in order to Y
someone/something will X in order to Y
: Activity:Stage  X is one step/action/part of the actions in Y
X is one step/action/part of the actions in Y
: Agent Attribute:State  a person who is X often is in a state of Y
a person who is X often is in a state of Y
: Agent:Goal  Y is the goal of X
Y is the goal of X
: Agent:Instrument  an X uses Y to perform their role
an X uses Y to perform their role
: Agent:Object  an X makes Y / an X uses Y to make an item
an X makes Y / an X uses Y to make an item
: Agent:Recipient  a Y receives an item/knowledge/service from X
Y receives an item/knowledge/service from X
: Agent/Object Attribute: Atypical Action (adjective:verb)  someone/something who is X is unlikely to Y
someone/something who is X is unlikely to Y
: Agent/ObjectAttribute:Typical Action  something/someone that is X will typically Y
something/someone that is X will typically Y
: Asymmetric Contrary  X and Y are at opposite ends of the same scale but X is more extreme than Y
X and Y are at opposite ends of the same scale but X is more extreme than Y
: Attachment  an X is attached to a Y
an X is attached to a Y
: Attribute Similarity  X and Y both have a similar attribute or feature
X and Y both have a similar attribute or feature
: Attribute:Nonstate (adjective:noun)  someone/something who is X cannot be Y or be in the state of Y
someone/something who is X cannot be Y or be in the state of Y
: capital-common-countries
W:capital
: capital-world
W:capital-all
: Cause:Compensatory Action  X causes/compels a person to Y
X causes/compels a person to Y
: Cause:Effect  an X causes Y
an X causes Y
: Change  an X is an increase/decease in Y
an X is an increase/decease in Y
: city-in-state
W:city-in-state
: ClassIndividual  Y is a specific X
Y is a specific X
: Collection:Member  X is made from a collection of Y
X is made from a collection of Y
: Concealment  X conceals a person/place/thing's Y
X conceals a person/place/thing's Y
: Contiguity  X and Y share a contiguous border
X and Y share a contiguous border
: Contradictory  Something cannot be/have/do X and Y at the same time
cannot X and Y at same time
: Contrary  X and Y are contrary / opposite to each other
X and Y are contrary
: Conversion  X will become / be converted into Y
X will become / be converted into Y
: Coordinates  X and Y are two distinct objects in the same category
X, Y same category
: Creature:Possession  X possesses/owns/has Y
X possesses/owns/has Y
: currency
W:currency
: D01 [noun+less_reg].txt
noun+less_reg
: D02 [un+adj_reg].txt
un+adj_reg
: D03 [adj+ly_reg].txt
adj+ly_reg
: D04 [over+adj_reg].txt
over+adj_reg
: D05 [adj+ness_reg].txt
adj+ness_reg
: D06 [re+verb_reg].txt
re+verb_reg
: D07 [verb+able_reg].txt
verb+able_reg
: D08 [verb+er_irreg].txt
verb+er_irreg
: D09 [verb+tion_irreg].txt
verb+tion_irreg
: D10 [verb+ment_irreg].txt
verb+ment_irreg
: Defective  an X is is a defect in Y
an X is is a defect in Y
: Dimensional Excessive  Y is an excessive form of X
Y is an excessive form of X
: Dimensional Naughty  Y is an unacceptable form of X
Y is an unacceptable form of X
: Dimensional Similarity  an X and Y are two kinds in a category of actions/things/attributes
X, Y two kinds in category
: Directional  X is the opposite direction from Y
X is opp.~dir.~from Y
: E01 [country - capital].txt
country - capital
: E02 [country - language].txt
country - language
: E03 [UK_city - county].txt
UK_city - county
: E04 [name - nationality].txt
name - nationality
: E05 [name - occupation].txt
name - occupation
: E06 [animal - young].txt
animal - young
: E07 [animal - sound].txt
animal - sound
: E08 [animal - shelter].txt
animal - shelter
: E09 [things - color].txt
things - color
: E10 [male - female].txt
male - female
: EnablingAgent:Object  X enables the use of Y
X enables the use of Y
: Event:Feature  Y is typically found at an event such as X
Y is typically found at an event such as X
: Expression  X is an expression that indicates Y
X is an expression that indicates Y
: family
W:family
: Functional  Y functions as an X
Y functions as an X
: gram1-adjective-to-adverb
W:adj-to-adverb
: gram2-opposite
W:opposite
: gram3-comparative
W:comparative
: gram4-superlative
W:superlative
: gram5-present-participle
W:pres-participle
: gram6-nationality-adjective
W:nationality-adj
: gram7-past-tense
W:past-tense
: gram8-plural
W:plural
: gram9-plural-verbs
W:plural-verbs
: I01 [noun - plural_reg].txt
noun - plural_reg
: I02 [noun - plural_irreg].txt
noun - plural_irreg
: I03 [adj - comparative].txt
adj - comparative
: I04 [adj - superlative].txt
adj - superlative
: I05 [verb_inf - 3pSg].txt
verb_inf - 3pSg
: I06 [verb_inf - Ving].txt
verb_inf - Ving
: I07 [verb_inf - Ved].txt
verb_inf - Ved
: I08 [verb_Ving - 3pSg].txt
verb_Ving - 3pSg
: I09 [verb_Ving - Ved].txt
verb_Ving - Ved
: I10 [verb_3pSg - Ved].txt
verb_3pSg - Ved
: Incompatible  Being X is incompatible with being Y
Being X is incompatible with being Y
: Instrument:Goal  X is intended to produce Y
X is intended to produce Y
: Instrument:Intended Action  Y is the intended action to be taken using X
do Y using X
: Item:Distinctive Nonpart  X is devoid of / cannot have Y
X is devoid of / cannot have Y
: Item:Ex-part/Ex-possession  an X once had/owned/possessed Y but no longer
an X once had/owned/possessed Y but no longer
: Item:Location  an X is a place/location/area where Y is found
X is a place/location/area where Y is found
: Item:Nonattribute (noun:adjective)  an X cannot have attribute Y; Y is antithetical to being X
an X cannot have attribute Y; Y is antithetical to being X
: Item:Topological Part  Y is one of the areas/locations of X
Y is one of the areas/locations of X
: ItemAttribute(noun:adjective)  an X has the attribute Y
an X has the attribute Y
: JJ_JJR
JJ_JJR
: JJ_JJS
JJ_JJS
: JJR_JJ
JJR_JJ
: JJR_JJS
JJR_JJS
: JJS_JJ
JJS_JJ
: JJS_JJR
JJS_JJR
: Knowledge  X is the name for knowledge of Y
X is knowledge of Y
: L01 [hypernyms - animals].txt
hypernyms - animals
: L02 [hypernyms - misc].txt
hypernyms - misc
: L03 [hyponyms - misc].txt
hyponyms - misc
: L04 [meronyms - substance].txt
meronyms - substance
: L05 [meronyms - member].txt
meronyms - member
: L06 [meronyms - part].txt
meronyms - part
: L07 [synonyms - intensity].txt
synonyms - intensity
: L08 [synonyms - exact].txt
synonyms - exact
: L09 [antonyms - gradable].txt
antonyms - gradable
: L10 [antonyms - binary].txt
antonyms - binary
: Location:Action/Activity  an X is a place/location/area where Y takes place
Y happens at X
: Location:Instrument/Associated Item  Y is an instrumental item in the activities that occur at place/location/area Y
Y is an instrumental item in the activities that occur at place/location/area Y
: Location:Process/Product  an X is a place/location/area where Y is made/done/produced
an X is a place/location/area where Y is made/done/produced
: Mass:Potion  X may be divided into Y
X may be divided into Y
: NN_NNS
NN_NNS
: NNS_NN
NNS_NN
: Object Attribute:Condition  something that is X may be Y
something that is X may be Y
: Object:Component  a Y is a part of an X
a Y is a part of an X
: Object:Instrument  a Y is used on an X
a Y is used on an X
: Object:Nonstate (noun:noun)  Y describes a condition or state that is usually absent from X
Y describes a condition or state that is usually absent from X
: Object:Recipient  an Y receives an X
an Y receives an X
: Object:Stuff  X is made of / is comprised of Y
X made of Y
: Object:Typical Action (noun.verb)  an X will typically Y
an X will typically Y
: ObjectAttribute:Noncondition (adjective:adjective)  something that is X cannot be Y
something that is X cannot be Y
: Objects:Atypical Action (noun:verb)  an X is unlikely to Y
an X is unlikely to Y
: ObjectState(noun:noun)  an X exists in the state of Y
an X exists in the state of Y
: Plan  an X is a plan for Y
an X is a plan for Y
: Plural Collective  Y are items in a collection/group of X
Y are items in a collection/group of X
: Prevention  X prevents Y
X prevents Y
: Pseudoantonym  X is similar to the opposite of Y but X is not truly the opposite of Y
X is similar to the opposite of Y but X is not truly the opposite of Y
: Recipient:Instrument  Y is an instrument through with X receives some object/service/role
Y is an instrument through with X receives some object/service/role
: Representation  a Y represents/is representative of X
a Y represents/is representative of X
: Reverse  X is the reverse act of Y / X may be undone by Y
X is the reverse act of Y / X may be undone by Y
: Sequence  an Y follows X in sequence
an Y follows X in sequence
: Sign:Significant  an X indicates/signifies Y
an X indicates/signifies Y
: Singular Collective  a Y is one item in a collection/group of X
a Y is one item in a collection/group of X
: Synonymity  an X and Y are a similar type of action/thing/attribute
X, Y similar type of thing
: Taxonomic  Y is a kind/type/instance of X
Y is a kind/type/instance of X
: Time Action/Activity  X is a time when Y occurs
X is a time when Y occurs
: Time Associated Item  Y is an item associated with time X
Y is an item associated with time X
: VB_VBD
VB_VBD
: VB_VBZ
VB_VBZ
: VBD_VB
VBD_VB
: VBD_VBZ
VBD_VBZ
: VBZ_VB
VBZ_VB
: VBZ_VBD
VBZ_VBD"""

catlist = cats.split('\n')

catmap = {a:b for a,b in zip(catlist[::2], catlist[1::2])}
catmapold = {re.sub('  ', ' ', a):b for a,b in zip(catlist[::2], catlist[1::2])}
catmap.update(catmapold)

text = ''.join([line for line in fileinput.input()])

for key, val in catmap.iteritems():
    val = re.sub('_', '\\_', val)
    text = text.replace(key, val)
    # text = re.sub(key, val, text)

print(text)
