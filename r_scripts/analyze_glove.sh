#!/usr/bin/env bash

Rscript w2v_pipeline.r
python csv_to_tabular.py table.csv | python rename_categories.py >mapped_glove_table.txt
open mapped_glove_table.txt
