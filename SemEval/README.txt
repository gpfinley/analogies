
===============================================================================

SemEval-2012 Task 2: Measuring Degrees of Relational Similarity

David A. Jurgens, Saif M. Mohammad, Peter D. Turney, and Keith J. Holyoak
April 5, 2012

This README.txt file describes the data files for SemEval-2012 Task 2. For 
information about SemEval-2012 Task 2, see the website
(https://sites.google.com/site/semeval2012task2/). This work is licensed under 
a Creative Commons Attribution-ShareAlike 3.0  Unported License
(http://creativecommons.org/licenses/by-sa/3.0/).

Summary

Degrees of relational similarity: the task is, given two pairs of words, 
A:B and C:D, determine the degree to which the semantic relations between 
A and B are similar to those between C and D. Unlike the more familiar task 
of semantic relation identification, which assigns each word pair to a 
discrete semantic relation class, this task recognizes the continuous range 
of degrees of relational similarity. The challenge is to determine the degrees 
of relational similarity between given reference word pairs and a variety of 
other pairs, mostly in the same general semantic relation class as the 
reference pair. 

Briefly, this download package, SemEval-2012-Complete-Data-Package.tar,
contains:

/Training/Phase1Questions/   - training data for 10 categories
/Training/Phase1Answers/     - training data for 10 categories
/Training/Phase2Questions/   - training data for 10 categories
/Training/Phase2Answers/     - training data for 10 categories

/Testing/Phase1Questions/    - testing data for 69 categories
/Testing/Phase1Answers/      - testing data for 69 categories
/Testing/Phase2Questions/    - testing data for 69 categories
/Testing/Phase2Answers/      - testing data for 69 categories

The data files have names containing letters, such as "1a", "2c", "2h".
These letters identify which of the 79 categories is involved. See
the following files for more information:

- subcategories-definitions.txt (brief definitions of the subcategories)
- subcategories-list.txt (list of the subcategories)
- subcategories-paradigms.txt (paradigm words given to Turkers)
- subcategories-training.txt (the 10 subcategories for training)

The file "Taxonomy.txt" contains additional examples of word pairs,
beyond the paradigm pairs that were given to the Turkers (in
subcategories-paradigms.txt). The additional examples might be helpful
in training your algorithm.

Dataset Creation

The dataset was created in two phases. In Phase 1, workers at Amazon's
Mechanical Turk (Turkers) were given paradigmatic examples of pairs that 
are instances of one of the categories, and they were asked to generate
further examples of the given category. In Phase 2, Turkers were given
the pairs from Phase 1 and asked to judge how similar the pairs are
to the paradigmatic examples. 

The file Questionnaire.pdf shows the questions that were given to the
Turkers for Phases 1 and 2. Each Phase consists of two questions. In
both Phases, the first question is a "check" question, designed to
"check" that the Turker understands the given relation, and also to
guide the Turker to the intended meaning of the given relation. When
the "check" question is answered incorrectly, the Turker's data
is discarded; we only use the data when the "check" questions are correctly
answered. The Phase1Answers data files only contain the answers to the 
second question. The Phase1Questions data files contain the correct answer 
to the "check" question.

The questions in Phase 2 (see Questionnaire.pdf) are MaxDiff questions.
For more information on MaxDiff, see the Wikipedia page on MaxDiff
(http://en.wikipedia.org/wiki/MaxDiff).

Directories

1. /Training/Phase1Questions/ and /Testing/Phase1Questions/
   - 79 of 79 categories
   - see Questionnaire.pdf
   - Turkers were asked to generate word pairs similar to given paradigm 
     word pairs

2. /Training/Phase1Answers/ and /Testing/Phase1Answers/
   - 79 of 79 categories
   - see Questionnaire.pdf;
   - these are the answers that the Turkers gave to the Phase 1 Questions
   - we also added some extra word pairs by swapping the order of some
     of the Turkers' answers, randomly selected
   - for example, if a Turker answered A:B, we might randomly add B:A
     to the list of answers
   - the idea is to see, in Phase 2, how important pair order is when
     judging the degree of prototypicality of a word pair

3. /Training/Phase2Questions/ and /Testing/Phase2Questions/
   - 79 of 79 categories
   - see Questionnaire.pdf
   - Turkers were asked MaxDiff questions, which tell us the degree
     of prototypicality for the word pairs in Phase1Answers

4. /Training/Phase2Answers/ and /Testing/Phase2Answers/
   - 79 of 79 categories
   - see Questionnaire.pdf
   - the Perl scripts show how the Turkers' answers in Phase2Answers
     can be converted to ratings of the degrees of prototypicality for 
     the word pairs in Phase1Answers
   - the problem with directly asking Turkers for numerical ratings is
     that different Turkers would use the numerical scales differently
     (some would be biased to low numbers and others would be biased
     to high numbers)
   - the MaxDiff questions avoid this bias

5. Examples
   - examples of the output of the Perl scripts
   - see README-scripts.txt

How to Use the Data

1. Phase1Questions
   - extract the paradigm pairs and/or the relation definitions from
     this directory and use this information to train or guide your
     algorithm

2. Phase1Answers
   - use your algorithm to compare these pairs to the paradigms or definitions 
     from the previous step and rate the word pairs in Phase1Answers according 
     to their degree of prototypicality for the given relation

3. Phase2Questions 
   - answer these MaxDiff questions, using your ratings from the preceding
     step
   - see the Perl scripts for examples

4. Phase2Answers
   - compare your answers to human answers
   - see the Perl scripts for examples

Scoring

See the file README-scripts.txt for information on scoring.

Other Files

1. summary.xls - this spreadsheet summarizes the results for the submissions
                 to SemEval 2012 Task 2

2. reversals.txt - some pairs were deliberately reversed, to test how
                   sensitive Turkers and algorithms are to the order
                   of the words in the pairs; for each subcategory, 5 pairs
                   were randomly selected and their reversals were added
                   to the subcategory

===============================================================================


