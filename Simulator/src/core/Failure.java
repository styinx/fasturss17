package core;

import type.FailureType;

class Failure {
    private FailureType type = FailureType.CHAOSMONKEY;
    private Microservice target = null;
    //private Timestamp attack = 0;

    Failure() {

    }
}