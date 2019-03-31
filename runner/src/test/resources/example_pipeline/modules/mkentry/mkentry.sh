#!/bin/bash

cd content

for (( i = 1; i <= $1; i++ ))
do
	mkdir entry$i
	cd entry$i
	
	mkdir content
	mkdir module_data
	
	cd ..
done

