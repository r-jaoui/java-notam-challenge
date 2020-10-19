# java-notam-challenge

## Description

In this project, a crawler program was created to gather the data of NOTAM identifiers from http://notamweb.aviation-civile.gouv.fr/

Both bonuses were impemented aswell :

  - On start, there is a command line option that lets the user select if he wants to use the crawler or to input a NOTAM identifier himself. (note : in both cases, NOTAM identifiers' syntax is verified, and the returned HTML page is checked to verify that a match is found for that specific identifier)
  - There are two options, *DEBUG*, that allows for a more descriptive console log (for example to verify that all fields are valid), and *SAVE*, that saves the resulting data onto a .csv file, in the output directory.
  
The Form Parameters for both the general and the single identifier requests are instances of the **FormParameters** interface, and implement the *parse()* method, that generate the full form parameter field. This allows for more modularity (for example if other types of requests must be sent, or if the parameters of the requests need to be changed "on the fly")

Requests are therefore sent via the same method (no matter the URL or the parameters, both are arguments).

## Repository

This repository contains the following folders :

  - **doc**, containing the project's compiled JavaDoc
  - **src**, containing all sources
  - **bin**, containing precompiled binaries
  - **output**, containing the resulting .csv files (one example is already on this folder)

## Updates

**19/10/2020** - Added control of the CSV Separator to be used (USA/UK standards or European standards), aswell as output files to show the difference.
