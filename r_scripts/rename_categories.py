import re
import fileinput

# Convert categories from their original names in the dataset to abbreviated names suitable for a table

cats = """: Action:Action Attribute  X is a Y kind of action
\\lexical{X is Y kind of action}
: Action:Action Nonattribute  X cannot be done in a Y manner
\\lexical{X cannot be done in Y manner}
: Action:Object  someone perform the action X on Y
\\lexical{action X performed on Y}
: Action:Object Attribute  someone will X an object that is Y
\\lexical{X an object that is Y}
: Action:Object Nonattribute  the result of action X does not produce an object with Y
\\lexical{action X not produce s/t with Y}
: Action:Recipient  to X is to have a Y receive some object/service/idea
\\lexical{to X is to have a Y receive some object/service/idea}
: Action:Resultant Attribute (verb:noun/adjective)  the action X results Y or things that are Y
\\lexical{the action X results Y or things that are Y}
: Action/Activity:Goal  someone/something will X in order to Y
\\lexical{someone/something will X in order to Y}
: Activity:Stage  X is one step/action/part of the actions in Y
\\lexical{X is one step/action/part of the actions in Y}
: Agent Attribute:State  a person who is X often is in a state of Y
\\lexical{a person who is X often is in a state of Y}
: Agent:Goal  Y is the goal of X
\\lexical{Y is the goal of X}
: Agent:Instrument  an X uses Y to perform their role
\\lexical{an X uses Y to perform their role}
: Agent:Object  an X makes Y / an X uses Y to make an item
\\lexical{an X makes Y / an X uses Y to make an item}
: Agent:Recipient  a Y receives an item/knowledge/service from X
\\lexical{Y receives an item/knowledge/service from X}
: Agent/Object Attribute: Atypical Action (adjective:verb)  someone/something who is X is unlikely to Y
\\lexical{someone/something who is X is unlikely to Y}
: Agent/ObjectAttribute:Typical Action  something/someone that is X will typically Y
\\lexical{something/someone that is X will typically Y}
: Asymmetric Contrary  X and Y are at opposite ends of the same scale but X is more extreme than Y
\\lexical{X and Y are at opposite ends of the same scale but X is more extreme than Y}
: Attachment  an X is attached to a Y
\\lexical{an X is attached to a Y}
: Attribute Similarity  X and Y both have a similar attribute or feature
\\lexical{X and Y both have a similar attribute or feature}
: Attribute:Nonstate (adjective:noun)  someone/something who is X cannot be Y or be in the state of Y
\\lexical{someone/something who is X cannot be Y or be in the state of Y}
: capital-common-countries
\\namedent{G:capital}
: capital-world
\\namedent{G:capital-all}
: Cause:Compensatory Action  X causes/compels a person to Y
\\lexical{X causes/compels a person to Y}
: Cause:Effect  an X causes Y
\\lexical{an X causes Y}
: Change  an X is an increase/decease in Y
\\lexical{an X is an increase/decease in Y}
: city-in-state
\\namedent{G:city-in-state}
: ClassIndividual  Y is a specific X
\\lexical{Y is a specific X}
: Collection:Member  X is made from a collection of Y
\\lexical{X is made from a collection of Y}
: Concealment  X conceals a person/place/thing's Y
\\lexical{X conceals a person/place/thing's Y}
: Contiguity  X and Y share a contiguous border
\\lexical{X and Y share a contiguous border}
: Contradictory  Something cannot be/have/do X and Y at the same time
\\lexical{cannot X and Y at same time}
: Contrary  X and Y are contrary / opposite to each other
\\lexical{X and Y are contrary}
: Conversion  X will become / be converted into Y
\\lexical{X will become Y}
: Coordinates  X and Y are two distinct objects in the same category
\\lexical{X, Y same category}
: Creature:Possession  X possesses/owns/has Y
\\lexical{X possesses/owns/has Y}
: currency
\\namedent{G:currency}
: D01 [noun+less_reg].txt
\\deriv{noun+less_reg}
: D02 [un+adj_reg].txt
\\deriv{un+adj_reg}
: D03 [adj+ly_reg].txt
\\deriv{adj+ly_reg}
: D04 [over+adj_reg].txt
\\deriv{over+adj_reg}
: D05 [adj+ness_reg].txt
\\deriv{adj+ness_reg}
: D06 [re+verb_reg].txt
\\deriv{re+verb_reg}
: D07 [verb+able_reg].txt
\\deriv{verb+able_reg}
: D08 [verb+er_irreg].txt
\\deriv{verb+er_irreg}
: D09 [verb+tion_irreg].txt
\\deriv{verb+tion_irreg}
: D10 [verb+ment_irreg].txt
\\deriv{verb+ment_irreg}
: Defective  an X is is a defect in Y
\\lexical{an X is is a defect in Y}
: Dimensional Excessive  Y is an excessive form of X
\\lexical{Y is an excessive form of X}
: Dimensional Naughty  Y is an unacceptable form of X
\\lexical{Y is an unacceptable form of X}
: Dimensional Similarity  an X and Y are two kinds in a category of actions/things/attributes
\\lexical{X, Y two kinds in category}
: Directional  X is the opposite direction from Y
\\lexical{X is opp.~dir.~from Y}
: E01 [country - capital].txt
\\namedent{country - capital}
: E02 [country - language].txt
\\namedent{country - language}
: E03 [UK_city - county].txt
\\namedent{UK_city - county}
: E04 [name - nationality].txt
\\namedent{name - nationality}
: E05 [name - occupation].txt
\\namedent{name - occupation}
: E06 [animal - young].txt
\\lexical{animal - young}
: E07 [animal - sound].txt
\\lexical{animal - sound}
: E08 [animal - shelter].txt
\\lexical{animal - shelter}
: E09 [things - color].txt
\\lexical{things - color}
: E10 [male - female].txt
\\lexical{male - female}
: EnablingAgent:Object  X enables the use of Y
\\lexical{X enables the use of Y}
: Event:Feature  Y is typically found at an event such as X
\\lexical{Y is typically found at an event such as X}
: Expression  X is an expression that indicates Y
\\lexical{X is an expression that indicates Y}
: family
\\lexical{G:gender}
: Functional  Y functions as an X
\\lexical{Y functions as an X}
: gram1-adjective-to-adverb
\\deriv{G:adj-to-adverb}
: gram2-opposite
\\deriv{G:opposite}
: gram3-comparative
\\infl{G:comparative}
: gram4-superlative
\\infl{G:superlative}
: gram5-present-participle
\\infl{G:pres-participle}
: gram6-nationality-adjective
\\namedent{G:nationality-adj}
: gram7-past-tense
\\infl{G:past-tense}
: gram8-plural
\\infl{G:plural}
: gram9-plural-verbs
\\infl{G:plural-verbs}
: I01 [noun - plural_reg].txt
\\infl{noun - plural_reg}
: I02 [noun - plural_irreg].txt
\\infl{noun - plural_irreg}
: I03 [adj - comparative].txt
\\infl{adj - comparative}
: I04 [adj - superlative].txt
\\infl{adj - superlative}
: I05 [verb_inf - 3pSg].txt
\\infl{verb_inf - 3pSg}
: I06 [verb_inf - Ving].txt
\\infl{verb_inf - Ving}
: I07 [verb_inf - Ved].txt
\\infl{verb_inf - Ved}
: I08 [verb_Ving - 3pSg].txt
\\infl{verb_Ving - 3pSg}
: I09 [verb_Ving - Ved].txt
\\infl{verb_Ving - Ved}
: I10 [verb_3pSg - Ved].txt
\\infl{verb_3pSg - Ved}
: Incompatible  Being X is incompatible with being Y
\\lexical{Being X is incompatible with being Y}
: Instrument:Goal  X is intended to produce Y
\\lexical{X is intended to produce Y}
: Instrument:Intended Action  Y is the intended action to be taken using X
\\lexical{do Y using X}
: Item:Distinctive Nonpart  X is devoid of / cannot have Y
\\lexical{X is devoid of / cannot have Y}
: Item:Ex-part/Ex-possession  an X once had/owned/possessed Y but no longer
\\lexical{an X once had/owned/possessed Y but no longer}
: Item:Location  an X is a place/location/area where Y is found
\\lexical{X is a place/location/area where Y is found}
: Item:Nonattribute (noun:adjective)  an X cannot have attribute Y; Y is antithetical to being X
\\lexical{an X cannot have attribute Y; Y is antithetical to being X}
: Item:Topological Part  Y is one of the areas/locations of X
\\lexical{Y is one of the areas/locations of X}
: ItemAttribute(noun:adjective)  an X has the attribute Y
\\lexical{an X has the attribute Y}
: JJ_JJR
\\infl{JJ_JJR}
: JJ_JJS
\\infl{JJ_JJS}
: JJR_JJ
\\infl{JJR_JJ}
: JJR_JJS
\\infl{JJR_JJS}
: JJS_JJ
\\infl{JJS_JJ}
: JJS_JJR
\\infl{JJS_JJR}
: Knowledge  X is the name for knowledge of Y
\\lexical{X is knowledge of Y}
: L01 [hypernyms - animals].txt
\\lexical{hypernyms - animals}
: L02 [hypernyms - misc].txt
\\lexical{hypernyms - misc}
: L03 [hyponyms - misc].txt
\\lexical{hyponyms - misc}
: L04 [meronyms - substance].txt
\\lexical{meronyms - substance}
: L05 [meronyms - member].txt
\\lexical{meronyms - member}
: L06 [meronyms - part].txt
\\lexical{meronyms - part}
: L07 [synonyms - intensity].txt
\\lexical{synonyms - intensity}
: L08 [synonyms - exact].txt
\\lexical{synonyms - exact}
: L09 [antonyms - gradable].txt
\\lexical{antonyms - gradable}
: L10 [antonyms - binary].txt
\\lexical{antonyms - binary}
: Location:Action/Activity  an X is a place/location/area where Y takes place
\\lexical{Y happens at X}
: Location:Instrument/Associated Item  Y is an instrumental item in the activities that occur at place/location/area Y
\\lexical{Y is an instrumental item in the activities that occur at place/location/area Y}
: Location:Process/Product  an X is a place/location/area where Y is made/done/produced
\\lexical{an X is a place/location/area where Y is made/done/produced}
: Mass:Potion  X may be divided into Y
\\lexical{X may be divided into Y}
: NN_NNS
\\infl{NN_NNS}
: NNS_NN
\\infl{NNS_NN}
: Object Attribute:Condition  something that is X may be Y
\\lexical{something that is X may be Y}
: Object:Component  a Y is a part of an X
\\lexical{a Y is a part of an X}
: Object:Instrument  a Y is used on an X
\\lexical{a Y is used on an X}
: Object:Nonstate (noun:noun)  Y describes a condition or state that is usually absent from X
\\lexical{Y describes a condition or state that is usually absent from X}
: Object:Recipient  an Y receives an X}
\\lexical{an Y receives an X}
: Object:Stuff  X is made of / is comprised of Y
\\lexical{X made of Y}
: Object:Typical Action (noun.verb)  an X will typically Y
\\lexical{an X will typically Y}
: ObjectAttribute:Noncondition (adjective:adjective)  something that is X cannot be Y
\\lexical{something that is X cannot be Y}
: Objects:Atypical Action (noun:verb)  an X is unlikely to Y
\\lexical{an X is unlikely to Y}
: ObjectState(noun:noun)  an X exists in the state of Y
\\lexical{an X exists in the state of Y}
: Plan  an X is a plan for Y
\\lexical{an X is a plan for Y}
: Plural Collective  Y are items in a collection/group of X
\\lexical{Y are items in a collection/group of X}
: Prevention  X prevents Y
\\lexical{X prevents Y}
: Pseudoantonym  X is similar to the opposite of Y but X is not truly the opposite of Y
\\lexical{X is similar to the opposite of Y but X is not truly the opposite of Y}
: Recipient:Instrument  Y is an instrument through with X receives some object/service/role
\\lexical{Y is an instrument through with X receives some object/service/role}
: Representation  a Y represents/is representative of X
\\lexical{a Y represents/is representative of X}
: Reverse  X is the reverse act of Y / X may be undone by Y
\\lexical{X is the reverse act of Y / X may be undone by Y}
: Sequence  an Y follows X in sequence
\\lexical{an Y follows X in sequence}
: Sign:Significant  an X indicates/signifies Y
\\lexical{an X indicates/signifies Y}
: Singular Collective  a Y is one item in a collection/group of X
\\lexical{a Y is one item in a collection/group of X}
: Synonymity  an X and Y are a similar type of action/thing/attribute
\\lexical{X, Y similar type of thing}
: Taxonomic  Y is a kind/type/instance of X
\\lexical{Y is a kind/type/instance of X}
: Time Action/Activity  X is a time when Y occurs
\\lexical{X is a time when Y occurs}
: Time Associated Item  Y is an item associated with time X
\\lexical{Y is an item associated with time X}
: VB_VBD
\\infl{VB_VBD}
: VB_VBZ
\\infl{VB_VBZ}
: VBD_VB
\\infl{VBD_VB}
: VBD_VBZ
\\infl{VBD_VBZ}
: VBZ_VB
\\infl{VBZ_VB}
: VBZ_VBD
\\infl{VBZ_VBD}"""

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
