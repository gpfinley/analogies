#!/usr/bin/env bash

rscript w2v_pipeline.r
python csv_to_tabular.py table.csv | python rename_categories.py >mapped_w2v_table.txt
open mapped_w2v_table.txt
