import ballerina/http;

listener http:Listener ep0 = new (8290, config = {host: "localhost"});

service /stockquote on ep0 {
    resource function get /view/[string symbol]() returns any|error {

    }

    resource function post /orders() returns any|error {

    }

    resource function get .() returns any|error {

    }

}

listener http:Listener ep1 = new (9090, config = {host: "localhost"});

service /sampleapi on ep1 {
    resource function put /sample/[string val1]/groups/[string val2]/name(string q1, string q2) returns any|error {

    }

    resource function get /sample/[string val1]/groups/[string val2]/name(string q1, string q2) returns any|error {

    }

}
