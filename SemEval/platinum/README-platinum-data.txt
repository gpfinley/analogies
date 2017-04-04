
===============================================================================

SemEval-2012 Task 2: Measuring Degrees of Relational Similarity

David A. Jurgens, Saif M. Mohammad, Peter D. Turney, and Keith J. Holyoak
May 17, 2012

This README.txt file describes an addendum to the data files for SemEval-2012
Task 2. For information about SemEval-2012 Task 2, see the website
(https://sites.google.com/site/semeval2012task2/). This work is licensed under a
Creative Commons Attribution-ShareAlike 3.0 Unported License
(http://creativecommons.org/licenses/by-sa/3.0/).

Summary

At the time of the SemEval Task 2 finalization, the MaxDiff annotation procedure
was still ongoing. We had aimed for five MTurk responses per MaxDiff question,
but averaged 4.73 responses per subcategory, with a minimum of 3.45 responses.
We continued gathering data after the Task 2 results were released to acquire an
additional 2,385 MaxDiff responses. We are releasing these additional responses
along with the existing responses as the Platinum dataset (in comparison to the
current "gold" dataset). 

We calculated the Spearman rank correlations between the prototypicality ratings
of the gold and platinum datasets and found that all subcategories have
correlations of at least 0.941469, indicating that the findings from gold
dataset should be consistent with those of the Platinum dataset.  As some pairs
did change ranks, however, we are releasing this data as a more complete set
of rating for future research in this area.


platinum-data-correlations.csv

- Spearman rank correlations between numeric protypicality ratings in the gold
  and platinum datasets for all 79 subcategories

/Platinum/Phase2Answers/ 

- MaxDiff responses for all 79 subcategories with the additional platinum data
included

/Platinum/Phase2AnswersScaled/ 

- Numeric prototypicality ratings for all 79 subcategories, computed using the
data in /Platinum/Phase2Answers/
