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


        /*
            Verify Generators
         */
        //Walk over all generators
        outerLoop : for (int i = 0; i < parser.generators.length; i++ ){
            //Know that searched for microservice exists  Microservice
            if(microserviceNames.contains(parser.generators[i].getMicroservice())){
                //Walk over all Microservices to get correct one
                for(int j = 0; j < parser.microservices.length ; j++){
                    //found correct micro service
                    if(parser.microservices[j].getName().equals(parser.generators[i].getMicroservice())){
                        //Walk over all operations
                        for (int k = 0; k < parser.microservices[j].getOperations().length; k++){
                            //Search for operation in correctly found micro service
                            if(parser.microservices[j].getOperations()[k].getName().equals(parser.generators[i].getOperation())){
                                continue outerLoop;
                            }

                            //Last run was unsucessfull
                            if(k == parser.microservices[j].getOperations().length -1){
                                System.out.println("ERROR GENERATORS: Could not find operation : "
                                        + parser.generators[i].getOperation() + " : in micro service : " + parser.microservices[j].getName() );
                                errorCounter++;
                            }
                        }
                    }
                }
            } else {
                System.out.println("ERROR GERNERATORS: Could not find microservice : " + parser.generators[i].getMicroservice() + " : from generator number: " + i );
                errorCounter++;
            }
        }

        /*
         Verify Chaos Monkeys
         */
        //Walk over all chaos monkeys
        for(int i = 0; i < parser.monkeys.length; i++){
            //Check whether the time of current monkey lies in between the bounds of simulation
            if(parser.monkeys[i].getTime() >= Double.parseDouble(parser.simulation.get("duration"))){
                System.out.println("WARNING CHAOSMONKEYS: Monkey number: " + i + " tries to shutdown instances after simulation has been finished. Semantic error");
            }

            if(microserviceNames.contains(parser.monkeys[i].getMicroservice())){
                //walk over all microserives to adress correct one
                for(int j = 0; j < parser.microservices.length; j++){
                    //adress correct micro service
                    if(parser.monkeys[i].getMicroservice().equals(parser.microservices[j].getName())){
                        //check if number of instances that are going to be killed are bigger than existing instances
                        if(parser.monkeys[i].getInstances() > parser.microservices[j].getInstances()){
                            System.out.println("WARNING CHAOSMONEKYS: Monkey number : " + i + " tries to kill more instances then available");
                        }
                    }
                }
            } else {
                System.out.println("ERROR CHAOSMONKEYS: Could not find microservice : " + parser.monkeys[i].getMicroservice() + " : in chaos monkey number: " + i);
                errorCounter++;
            }

        }


        /*
        Verify microservice graph architekture
         */
        //Walk over all micro services
        for(int microService = 0; microService < parser.microservices.length; microService++){
            //walk over all operations
            for(int operation = 0; operation < parser.microservices[microService].getOperations().length; operation++){
                //walk over all dependencies
                for(int dependencie = 0; dependencie < parser.microservices[microService].getOperations()[operation].getDependencies().length; dependencie ++ ) {
                    //get targeted micro service
                    String tempMicroserviceName = parser.microservices[microService].getOperations()[operation].getDependencies()[dependencie].get("service");
                    //get targeted operation name
                    String tempOperationName = parser.microservices[microService].getOperations()[operation].getDependencies()[dependencie].get("operation");

                    if(microserviceNames.contains(tempMicroserviceName)){
                        //Named Microservice has been found now check for operation in named microservice

                        //walk over all micro services
                        loop: for(int i = 0; i < parser.microservices.length; i++){
                            //found micro service with name equal to target micro service
                            if(tempMicroserviceName.equals(parser.microservices[i].getName())){
                                //run over all operations in target micro service

                                for(int j = 0; j < parser.microservices[i].getOperations().length; j ++){
                                    if(tempOperationName.equals(parser.microservices[i].getOperations()[j].getName())){
                                        break loop;
                                    } else {
                                        //Last loop run was unsucessfull
                                        if(j == parser.microservices[i].getOperations().length-1){
                                            System.out.println("ERROR MICROSERIVCES: Could not finde operation : " + tempOperationName + " : in micro service : "
                                                    + tempMicroserviceName + " : in a dependencie in : " +parser.microservices[microService].getName());
                                            errorCounter++;
                                            break loop;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("ERROR MICROSERVICES: Could not finde microservice : " + tempMicroserviceName +
                                " : located in a dependencie in : " + parser.microservices[microService].getName());
                        errorCounter++;
                    }
                }
            }
        }

        if(errorCounter > 0){
            System.out.println("Syntax error count: " + errorCounter );
            return false;
        } else {
            return true;
        }
    }
}
