# stock-trends-analyzer
Pulls and detects stock trends over user-defined time periods, for a set of user-defined stock symbols, and dumps the output to separate CSV files

## Requirements
Text file containing stock symbols for each organization

## Run Command
```
./build/scripts/stock-trends-analyzer -configFilePath ./src/main/resources/appconfig.json -symbolsFilePath ./src/main/resources/symbols.txt -startDate 2010-01-01 -endDate 2016-12-31 -outputDirectory .
```
