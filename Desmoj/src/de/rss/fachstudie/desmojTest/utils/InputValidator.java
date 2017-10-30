package de.rss.fachstudie.desmojTest.utils;

import java.util.ArrayList;
import java.util.List;

public class InputValidator {

    /*
        Returns true / false based on the correct dependencies of input file
        It will be checked:
        -All dependencies (per operation) depend on an existing micro service
        -The dependent micro service has the operation on which the dependencie points
     */
    public boolean valideInput(InputParser parser){

        List<String> microserviceNames = new ArrayList<String>();
        int errorCounter = 0;

        //Add all existing microservice names into a list
        for(int n = 0; n < parser.microservices.length; n++){
            microserviceNames.add(parser.microservices[n].getName());
        }

        //Walk over all micro services
        for(int microService = 0; microService < parser.microservices.length; microService++){
            //walk over all operations
            for(int operation = 0; operation < parser.microservices[microService].getOperations().length; operation++){
                //walk over all dependencies
                for(int dependencie = 0; dependencie < parser.microservices[microService].getOperations()[operation].getDependencies().length; dependencie ++ ) {
                    //get targeted micro service
                    String tempMicroserviceName = parser.microservices[microService].getOperations()[operation].getDependencies()[dependencie].get("service");
                    //get targeted operation name
                    String tempOperationName = parser.microservices[microService].getOperations()[operation].getDependencies()[dependencie].get("name");

                    if(microserviceNames.contains(tempMicroserviceName)){
                        //Named Microservice has been found now check for operation in named microservice

                        //walk over all micro services
                        loop: for(int i = 0; i < parser.microservices.length; i++){
                            //found micro service with name equal to target micro service
                            if(tempMicroserviceName.equals(parser.microservices[i].getName())){
                                //System.out.println("Found correct MS and now looking for -- " + tempOperationName + " -- in MS: " + InputParser.microservices[i].getName());
                                //run over all operations in target micro service
                                for(int j = 0; j < parser.microservices[i].getOperations().length; j ++){
                                    //System.out.println(InputParser.microservices[i].getOperations()[j].getName());
                                    if(tempOperationName.equals(parser.microservices[i].getOperations()[j].getName())){
                                        //System.out.println("Found " + tempOperationName + " in " + InputParser.microservices[i].getName() +"------" );
                                        break loop;
                                    } else {
                                        //Last loop run was unsucessfull
                                        if(j == parser.microservices[i].getOperations().length-1){
                                            System.out.println("ERROR could not finde operation : " + tempOperationName + " : in micro service : " + tempMicroserviceName);
                                            errorCounter++;
                                            break loop;
                                        }
                                    }


                                }
                            }


                        }

                    } else {
                        System.out.println("ERROR could not finde micro service :: " + tempMicroserviceName);
                        errorCounter++;
                    }
                }
            }
        }

        if(errorCounter > 0){
            System.out.println("Error count: " + errorCounter );
            return false;
        } else {
            return true;
        }
    }



}
