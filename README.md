# MTGSearch
Final project for Mobile Dev


Card Rules lookup program for android devices for the popular card game Magic: The Gathering.
the program handles reading in card data either through a user entering the cards name into a search box, or by taking a picture 
of a card and extracting data using Optical Character Recognition using the Tesseract library, it then uses this data to query a 
database API and pull back and format the data to be displayed to the user. It also maintains a local database containing the most
recent searches and displays these in a scrolling list on the home screen when in landscape mode only.
